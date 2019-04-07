(
//  takes mono signal and plays it at at slightly different rates over 2 channels
title: "Stereo Float Sampler with ADSR Envelope",

name: "stereoFloatAdsr",

makeSynthDef: { arg self, name, sampler;

	SynthDef(name, {
		arg amp=1.0, start=0, freq=440,
		gate=1, attackTime=0.1, decayTime=0.2, sustainLevel=0.8, curve= -4,
		out=~ss.bus.master;

		var mySample, buffer, bufferFreq, rate, panFreqRange=(sampler.panFreqRange ? 1.001), sig;

		var releaseTime = \releaseTime.kr(sampler.releaseTime);

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

		// the adsr envelope
		sig = sig * EnvGen.kr(
			Env.adsr(attackTime, decayTime, sustainLevel, releaseTime, curve:curve),
			gate:gate, levelScale:amp, doneAction:2);

		Out.ar(out, sig);

	}).add;

},


)