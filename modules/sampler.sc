(

// namespace: ["buf"],
title: "Sample utilities", // friendly name


initModule: { | self |

},


makeSampler: {

	arg self, name, buffers, buffer_freqs;

	SynthDef(name, {
		arg amp=1.0, start=0, freq=440, bus=~ss.bus.master;

		var mysize = buffers.size;

		var buffer, buffer_freq, rate, sig;

		var cutover_freqs = Array.fill(buffer_freqs.size-1,
			{ |i| buffer_freqs[i] + ((buffer_freqs[i+1] - buffer_freqs[i]) / 2) });

		cutover_freqs.postln;
		cutover_freqs[cutover_freqs.size-1].postln;

		buffer = buffers[0].bufnum * (freq < cutover_freqs[0]);
		buffer_freq = buffer_freqs[0] * (freq < cutover_freqs[0]);

		(mysize-2).do({ |i|
			buffer = buffer + (buffers[i+1].bufnum * (freq >= cutover_freqs[i]) * (freq < cutover_freqs[i+1]));
			buffer_freq = buffer_freq + (buffer_freqs[i+1] * (freq >= cutover_freqs[i]) * (freq < cutover_freqs[i+1]));
		});

		buffer = buffer + (buffers[mysize-1].bufnum * (freq >= cutover_freqs[cutover_freqs.size-1]));
		buffer_freq = buffer_freq + (buffer_freqs[mysize-1]* (freq >= cutover_freqs[cutover_freqs.size-1]));


		rate = freq / buffer_freq;

		sig = PlayBuf.ar(2,
			bufnum:buffer,
			rate:BufRateScale.kr(buffer)*rate,
			startPos:BufSampleRate.kr(buffer) * start,
			doneAction:2,
		);
		sig = sig * amp;
		Out.ar(bus, sig);

	}).add;
}

)
