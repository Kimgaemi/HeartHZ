package com.heart.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.os.Environment;

public class Converter {

	public static final String AUDIO_RECORDER_FOLDER = "HeartHZ"; // Folder Name

	public static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.pcm";
	public static final String AUDIO_RECORDER_FILE = "record.wav";
	public static final String AUDIO_MIXING_TEMP_FILE = "mixing_temp.pcm";
	public static final String AUDIO_SUMATION_TEMP_FILE = "sumation_temp.pcm";
	public static final String AUDIO_SUMATION_FILE = "record.wav";

	public static String AUDIO_MIXING_FILE = "mixing.wav";

	protected final int BGM_FACTOR = 1;
	protected final int RECORDER_BPP = 16;
	protected int frequency;
	protected int recBufSize;

	public Converter(int frequency, int recBufSize) {
		this.frequency = frequency;
		this.recBufSize = recBufSize;
	}

	/* 1. JUST RECORD PART */

	// GET PCM FILE LOCATION
	public String getTempFilename(String name) {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);
		if (!file.exists()) {
			file.mkdirs();
		}

		File tempFile = new File(filepath, AUDIO_RECORDER_TEMP_FILE);
		if (tempFile.exists())
			tempFile.delete();

		return (file.getAbsolutePath() + "/" + name);
	}

	// GET WAV FILE LOCATION
	public String getFilename(String name) {
		String filepath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);
		if (file.exists()) {
			file.delete();
		}
		return (file.getAbsolutePath() + "/" + name);
	}

	// DELETE FILE
	public void deleteFile(String root) {
		String filepath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER + "/" + root);
		if (file.exists()) {
			file.delete();
		}
	}

	/* 2. CONVERT PART */

	// WAV HEADER
	private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
			long totalDataLen, long longSampleRate, int channels, long byteRate)
			throws IOException {

		byte[] header = new byte[44];

		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (1 * 16 / 8); // block align
		header[33] = 0;
		header[34] = RECORDER_BPP; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
		out.write(header, 0, 44);
	}

	// WAV -> PCM CONVERT
	private void WavToPcmFile(String inFilename, String outFilename) {
		FileInputStream in = null;
		FileOutputStream out = null;
		byte[] tempdata = new byte[44];
		byte[] data = new byte[recBufSize];

		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			in.read(tempdata);

			while (in.read(data) != -1) {
				out.write(data);
			}

			in.close();
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// PCM -> WAV CONVERT
	private void PcmToWavFile(String inFilename, String outFilename) {
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = frequency;
		int channels = 1;
		long byteRate = RECORDER_BPP * frequency * channels / 8;

		byte[] data = new byte[recBufSize];

		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;

			// AppLog.logString("File size: " + totalDataLen);

			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);

			while (in.read(data) != -1) {
				out.write(data);
			}

			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// CONVERTING PCM -> WAV FILE
	public void convertPcmToWav() {
		PcmToWavFile(getTempFilename(AUDIO_RECORDER_TEMP_FILE),
				getFilename(AUDIO_RECORDER_FILE));
		deleteFile(AUDIO_RECORDER_TEMP_FILE);
	}

	// CONVERTING WAV -> PCM FILE
	public void convertWavToPcm(String path) {
		// WavToPcmFile(getFilename(AUDIO_RECORDER_FILE),
		// getTempFilename(AUDIO_RECORDER_TEMP_FILE));
		WavToPcmFile(path, getTempFilename(AUDIO_MIXING_TEMP_FILE));
	}

	// MIXING WAV FILE
	public void mixingWav() {
		mixingPcm(getTempFilename(AUDIO_RECORDER_TEMP_FILE),
				getTempFilename(AUDIO_MIXING_TEMP_FILE),
				getTempFilename(AUDIO_SUMATION_TEMP_FILE));
		PcmToWavFile(getTempFilename(AUDIO_SUMATION_TEMP_FILE),
				getFilename(AUDIO_SUMATION_FILE));
		deleteFile(AUDIO_RECORDER_TEMP_FILE);
		deleteFile(AUDIO_MIXING_TEMP_FILE);
		deleteFile(AUDIO_SUMATION_TEMP_FILE);
	}

	// MIXING LOGIC
	public void mixingPcm(String inFilename1, String inFilename2,
			String outFilename) {

		FileInputStream in1 = null;
		FileInputStream in2 = null;
		FileOutputStream out = null;

		byte[] data1 = new byte[recBufSize];
		byte[] data2 = new byte[recBufSize];

		byte[] outData = new byte[recBufSize];

		try {
			in1 = new FileInputStream(inFilename1);
			in2 = new FileInputStream(inFilename2);
			out = new FileOutputStream(outFilename);

			while (in1.read(data1) != -1) {
				in2.read(data2);
				// 두 음원 더하기
				short[] shortdata1 = new short[data1.length / 2];
				short[] shortdata2 = new short[data2.length / 2];
				short[] s_outData = new short[data1.length / 2];

				ByteBuffer.wrap(data1).order(ByteOrder.LITTLE_ENDIAN)
						.asShortBuffer().get(shortdata1);
				ByteBuffer.wrap(data2).order(ByteOrder.LITTLE_ENDIAN)
						.asShortBuffer().get(shortdata2);

				for (int i = 0; i < data1.length / 2; i++) {
					int temp = ((shortdata1[i]) + (shortdata2[i] >> BGM_FACTOR));
					if (s_outData[i] > 32767) {
						s_outData[i] = (short) 32767;
					} else {
						s_outData[i] = (short) temp;
					}

				}

				ByteBuffer.wrap(outData).order(ByteOrder.LITTLE_ENDIAN)
						.asShortBuffer().put(s_outData);
				out.write(outData);
			}

			in1.close();
			in2.close();
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
