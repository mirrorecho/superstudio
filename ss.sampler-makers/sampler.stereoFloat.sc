(
//  takes mono signal and plays it at at slightly different rates over 2 channels
title: "Stereo Float Sampler",

name: "stereoFloat",

makeSynthDef: { arg self, name, sampler;

	SynthDef(name, {
		arg amp=1.0, start=0, freq=440,
		out=~ss.bus.master;

		var mySample, buffer, bufferFreq, rate, panFreqRange=(sampler.panFreqRange ? 1.001), sig;

		mySample = sampler.getSample(freq);
		buffer = mySample[0];
		bufferFreq = mySample[1];

		rate = freq / bufferFreq;
		sig = PlayBuf.ar(
			numChannels: 1,
			bufnum: buffer,
			rate: BufRateScale.kr(buffer)*[rate*panFreqRange, rate/panFreqRange],
			startPos: BufSampleRate.ir(buffer) * start,
			doneAction:2,
		);
		sig = sig * amp;

		Out.ar(out, sig);

	}).add;

},


)