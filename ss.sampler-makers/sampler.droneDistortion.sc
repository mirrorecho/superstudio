(
title: "Drone Sampler with Distortion",

initModule: { arg self;
},

name: "droneDistortion",

makeSynthDef: { arg self, name, sampler;

	SynthDef(name, {
		arg amp=1.0, start=0, freq=440,
		gate=1, attackTime=0.1, decayTime=0.2, sustainLevel=1, curve= -4,
		out=~ss.bus.master;

		var mySample, buffer, bufferFreq, rate, length, envs, envTimes, sigs, sig, sigDistort;

		var releaseTime = \releaseTime.kr(sampler.releaseTime);

		// distortion amount (usable values generally 0 to 1)
		var distortion = \distortion.kr(sampler.distortion ? 0.4);

		mySample = sampler.getSample(freq);
		buffer = mySample[0];
		bufferFreq = mySample[1];

		rate = freq / bufferFreq;
		length = BufDur.ir(buffer);
		envTimes = (length / rate / 4)!3;

		sigs = 4.collect{ |i|
			PlayBuf.ar(
				numChannels: sampler.channels,
				bufnum: buffer,
				rate: BufRateScale.ir(buffer)*rate,
				startPos: BufSampleRate.ir(buffer) * length * i/4,
				loop: 1,
			);
		};

		// envelopes that cycle through through the total envelope times
		envs = [
			EnvGen.kr(Env.new(levels: [0, 0.25, 0.5, 0.25, 0], times: envTimes, curve: [6,-6,6,-6,]).circle),
			EnvGen.kr(Env.new(levels: [0.25, 0.5, 0.25, 0, 0.25], times: envTimes, curve: [-6,6,-6,6]).circle),
			EnvGen.kr(Env.new(levels: [0.5, 0.25, 0, 0.25, 0.5], times: envTimes, curve: [6,-6,6,-6]).circle),
			EnvGen.kr(Env.new(levels: [0.25, 0, 0.25, 0.5, 0.25], times: envTimes, curve: [-6,6,-6, 6,]).circle),
		];
		sigs = sigs * envs;

		// the final mixed signal with adsr envelope
		sig = Mix.ar(sigs);

		// the distorted signal
		sigDistort = (sig * (3 + (distortion * 40))).distort * (1-(distortion/1.4)) * 0.4;

		// mix between original sig and sigDistort
		sig = (sig * (1-distortion)) + (sigDistort * distortion);

		// the ADSR envelope
		sig = sig * EnvGen.kr(
			Env.adsr(attackTime, decayTime, sustainLevel, releaseTime, curve:curve),
			gate:gate, levelScale:amp*sampler.ampScale, doneAction:2);

		Out.ar(out, sig);

	}).add;
},

)

