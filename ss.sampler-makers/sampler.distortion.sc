(
title: "Sampler with Distortion",

name: "distortion",

makeSynthDef: { arg self, name, sampler;

	SynthDef(name, {
		arg amp=1.0, start=0, freq=440,
		gate=1, attackTime=0.1, decayTime=0.2, sustainLevel=0.8, releaseTime=1, curve= -4,
		out=~ss.bus.master;

		var mySample, buffer, bufferFreq, rate, sig, sigDistort;

		// distortion amount (usable values generally 0 to 1)
		var distortion = \distortion.kr(sampler.distortion ? 0.4);

		mySample = sampler.getSample(freq);
		buffer = mySample[0];
		bufferFreq=mySample[1];

		rate = freq / bufferFreq;
		sig = PlayBuf.ar(
			numChannels: sampler.channels,
			bufnum: buffer,
			rate: BufRateScale.kr(buffer)*rate,
			startPos: BufSampleRate.ir(buffer) * start,
			doneAction: 2,
		);

		// the distorted signal
		sigDistort = (sig * (3 + (distortion * 40))).distort * (1-(distortion/1.4)) * 0.4;

		// mix between original sig and sigDistort
		sig = (sig * (1-distortion)) + (sigDistort * distortion);

		sig = sig * amp;

		Out.ar(out, sig);

	}).add;

};

)