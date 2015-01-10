package com.github.davidmoten.ar;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import rx.functions.Func1;

public class AudioTest {

	@Test
	public void testReadUsingObservable() {
		int count = Audio
				.readSignal(
						AudioTest.class.getResourceAsStream("/alphabet.wav"))
				.count().toBlocking().single();
		System.out.println("count=" + count);
		assertEquals(296934, count);
	}

	@Test
	public void testReadUsingObservableAndFft() {
		Func1<List<Integer>, List<Double>> toFft = new Func1<List<Integer>, List<Double>>() {

			@Override
			public List<Double> call(List<Integer> signal) {
				if (signal.size() == 1024) {
					Complex[] spectrum = FFT.fft(Complex.toComplex(signal));
					ArrayList<Double> list = new ArrayList<Double>(
							spectrum.length);
					for (Complex c : spectrum)
						list.add(c.abs());

					return list;
				} else
					return Collections.emptyList();
			}
		};
		Audio.readSignal(AudioTest.class.getResourceAsStream("/alphabet.wav"))
		// buffer
				.buffer(1024).map(toFft).subscribe();
		;
	}
}
