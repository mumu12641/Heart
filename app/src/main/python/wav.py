import numpy as np
import wave
from scipy import signal


def read_integers_from_file(filename):
    integers = []
    with open(filename, 'r') as file:
        for line in file:
            if int(line.strip()) != 0:
                integers.append(int(line.strip()))  # 将每一行的内容转换为整数并添加到列表中
    return integers
def linear_interpolation(int_array, factor):
    upsampled_array = []
    for i in range(len(int_array) - 1):
        for j in range(factor):
            value = int_array[i] + (int_array[i + 1] - int_array[i]) * (j / factor)
            upsampled_array.append(value)
    upsampled_array.append(int_array[-1])
    return upsampled_array
def linear_map(x, in_min, in_max, out_min, out_max):
    return int((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min)

def txt2wav(txt_path, wav_path):
    integers = read_integers_from_file(txt_path)
    integers = [x for x in integers if x != 0]

    factor = 47
    integers = linear_interpolation(integers, factor)

    target_min = -32768
    target_max = 32767
    min_val = min(integers)
    max_val = max(integers)

    normalized_array = [linear_map(x, min_val, max_val, target_min, target_max) for x in integers]

    nyquist_freq = 0.5 * 8000
    low_cutoff = 15 / nyquist_freq
    high_cutoff = 200 / nyquist_freq
    b, a = signal.butter(4, [low_cutoff, high_cutoff], btype='band')

    filtered_signal = signal.filtfilt(b, a, normalized_array)

    # write_byte_array_to_pcm(int_list_to_byte_array(filtered_signal), pcm_path)

    with wave.open(wav_path, 'w') as wav_file:
        wav_file.setnchannels(1)  # 单声道
        wav_file.setsampwidth(2)   # 16 位量化
        wav_file.setframerate(8000)  # 8 kHz 采样率
        wav_file.setnframes(len(filtered_signal))
        wav_file.writeframes(np.array(normalized_array, dtype=np.int16).tobytes())