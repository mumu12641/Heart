import numpy as np
import wave
from scipy import signal

# def int_list_to_byte_array(int_list):
#     byte_array = bytearray(len(int_list) * 2)
#     for index, value in enumerate(int_list):
#         short_value = value & 0xFFFF
#         byte_array[index * 2] = short_value & 0xFF
#         byte_array[index * 2 + 1] = (short_value >> 8) & 0xFF
#     return byte_array
#
# def write_byte_array_to_pcm(byte_array, file_path):
#     with open(file_path, 'wb') as pcm_file:
#         pcm_file.write(byte_array)

def read_integers_from_file(filename):
    integers = []
    with open(filename, 'r') as file:
        for line in file:
            if int(line.strip()) != 0:
                integers.append(int(line.strip()))
    return integers
def linear_interpolation(int_array, factor):
    if factor == 1:
        return int_array
    upsampled_array = []
    for i in range(len(int_array) - 1):
        for j in range(factor):
            value = int_array[i] + (int_array[i + 1] - int_array[i]) * (j / factor)
            upsampled_array.append(value)
    upsampled_array.append(int_array[-1])
    return upsampled_array
def linear_map(x, in_min, in_max, out_min, out_max):
    return int((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min)


def write2wav(integers,wav_path, factor, freq):
    integers = linear_interpolation(integers, factor)

    target_min = -32768
    target_max = 32767
    min_val = min(integers)
    max_val = max(integers)

    normalized_array = [linear_map(x, min_val, max_val, target_min, target_max) for x in integers]

    # nyquist freq
    nyquist_freq = 0.5 * freq
    low_cutoff = 15 / nyquist_freq
    high_cutoff = 200 / nyquist_freq
    b, a = signal.butter(4, [low_cutoff, high_cutoff], btype='band')

    filtered_signal = signal.filtfilt(b, a, normalized_array)

    with wave.open(wav_path, 'w') as wav_file:
        wav_file.setnchannels(1)
        wav_file.setsampwidth(2)
        # wav freq
        wav_file.setframerate(freq)
        wav_file.setnframes(len(normalized_array))
        wav_file.writeframes(np.array(normalized_array, dtype=np.int16).tobytes())


def txt2wav(txt_path, wav_path, real_wav_path, pcm_path):
    integers = read_integers_from_file(txt_path)
    integers = [x for x in integers if x != 0]

    write2wav(integers, wav_path, 1, 2000)
    write2wav(integers, real_wav_path, 4, 8000)
