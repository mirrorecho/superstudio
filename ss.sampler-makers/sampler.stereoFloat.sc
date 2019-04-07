(
//  basic sampler that takes mono signal and plays it at at slightly different rates over 2 channels
title: "Stereo Float Sampler",

name: "stereoFloat",

makeSynthDef: { arg self, name, sampler;

	SynthDef(name, {
		arg amp=1.0, start=0, freq=440, out=~ss.bus.master;
		var mySample, buffer, buffer_freq, rate, panFreqRange=1.01, sig;

		mySample = sampler.getSample(freq);
		buffer = mySample[0];
		buffer_freq  =mySample[1];

		rate = freq / buffer_freq;
		sig = PlayBuf.ar(1,
			bufnum:buffer,
			rate:BufRateScale.kr(buffer)*[rate*panFreqRange, rate/panFreqRange],
			startPos:BufSampleRate.kr(buffer) * start,
			doneAction:2,
		);
		sig = sig * amp;

		Out.ar(out, sig);

	}).add;

},


)