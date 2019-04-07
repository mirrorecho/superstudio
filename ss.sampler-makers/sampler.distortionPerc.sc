(
title: "Sampler with Distortion and Percussive Envelope",

name: "distortionPerc",

makeSynthDef: { arg self, name, sampler;

	SynthDef(name, {
		arg amp=1.0, start=0, freq=440, attackTime=0.001, releaseTime=4, curve= -1, out=~ss.bus.master;
		var mySample, buffer, bufferFreq, rate, sig, sigDistort;
		var distortion = \distortion.kr(sampler.distortion ? 0.4);

		mySample = sampler.getSample(freq);
		buffer = mySample[0];
		bufferFreq=mySample[1];

		rate = freq / bufferFreq;
		sig = PlayBuf.ar(sampler.channels,
			bufnum:buffer,
			rate:BufRateScale.kr(buffer)*rate,
			startPos:BufSampleRate.kr(buffer) * start,
			doneAction:2,
		);

		// the distorted signal
		sigDistort = (sig * (3 + (distortion * 40))).distort * (1-(distortion/1.4)) * 0.4;

		// mix between original sig and sigDistort
		sig = (sig * (1-distortion)) + (sigDistort * distortion);

		// the percussive envelope:
		sig = sig * EnvGen.kr(
			Env.perc(attackTime:attackTime, releaseTime:releaseTime, level:amp, curve:curve),
			doneAction: 2);

		Out.ar(out, sig);

	}).add;

};

)