//
// Created by 12424 on 2024/4/7.
//
#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <malloc.h>
#include "include/wave.h"

char *dummy_get_raw_pcm(const char *pcmPath, int *bytes_read) {

    long lSize;
    char *pcm_buf;
    size_t result;
    FILE *fp_pcm;

    fp_pcm = fopen(pcmPath, "rb");
    if (fp_pcm == NULL) {
        printf("File error");
        return NULL;
    }

    // obtain file size:
    fseek(fp_pcm, 0, SEEK_END);
    lSize = ftell(fp_pcm);
    rewind(fp_pcm);

    // allocate memory to contain the whole file:
    pcm_buf = (char *) malloc(sizeof(char) * lSize);
    if (pcm_buf == NULL) {
        printf("Memory error");
        return NULL;
    }

    // copy the file into the pcm_buf:
    result = fread(pcm_buf, 1, lSize, fp_pcm);
    if (result != lSize) {
        printf("Reading error");
        return NULL;
    }

    *bytes_read = (int) lSize;
    return pcm_buf;
}

void set_wav_header(int raw_sz, wav_header_t *wh) {
    // RIFF chunk
    strncpy((char*)wh->chunk_id, "RIFF",strlen("RIFF"));
    wh->chunk_size = 36 + raw_sz;

    // fmt sub-chunk (to be optimized)
    strncpy((char *)wh->sub_chunk1_id, "WAVEfmt ", strlen("WAVEfmt "));
    wh->sub_chunk1_size = 16;
    wh->audio_format = 1;
    wh->num_channels = 1;
//    wh->sample_rate = 16000;
    wh->sample_rate = 8000;
    wh->bits_per_sample = 16;
    wh->block_align = wh->num_channels * wh->bits_per_sample / 8;
    wh->byte_rate = wh->sample_rate * wh->num_channels * wh->bits_per_sample / 8;

    // data sub-chunk
    strncpy((char *)(wh->sub_chunk2_id), "data", strlen("data"));
    wh->sub_chunk2_size = raw_sz;
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_mumu12641_util_FileUtil_pcmToWavJNI(JNIEnv *env, jobject thiz, jstring pcm_path,
                                                   jstring wav_path) {
    const char *pcmPath = env->GetStringUTFChars(pcm_path, NULL);
    const char *wavPath = env->GetStringUTFChars(wav_path, NULL);
    int raw_sz = 0;
    FILE *wav;
    wav_header_t header;

    memset(&header, '\0', sizeof(wav_header_t));
    char *pcm_buf = dummy_get_raw_pcm(pcmPath, &raw_sz);
    if(pcm_buf == NULL){
        return -1;
    }
    set_wav_header(raw_sz, &header);

    wav = fopen(wavPath, "wb");
    fwrite(&header, 1, sizeof(header), wav);
    fwrite(pcm_buf, 1, raw_sz, wav);
    fclose(wav);

    free(pcm_buf);
    return 1;
}

