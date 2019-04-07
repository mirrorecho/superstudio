(
title: "No-frills Sampler with Percussive Envelope",

name: "perc",

makeSynthDef: { arg self, name, sampler;

	SynthDef(name, {
		arg amp=1.0, start=0, freq=440,
		attackTime=0.001, curve= -2,
		out=~ss.bus.master;

		var mySample, buffer, bufferFreq, rate, sig;

		var releaseTime = \releaseTime.kr(sampler.releaseTime);

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

		// the percussive envelope:
		sig = sig * EnvGen.kr(
			Env.perc(attackTime:attackTime, releaseTime:releaseTime, curve:curve),
			levelScale:amp, doneAction: 2);


		Out.ar(out, sig);

	}).add;
},

)