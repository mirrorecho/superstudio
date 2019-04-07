(
title: "Combo of Drone and Stereo Fload Samplers",

initModule: { arg self;
},

name: "droneStereoFloat",

makeSynthDef: { arg self, name, sampler;

	SynthDef(name, {
		arg amp=1.0, start=0, freq=440,
		gate=1, attackTime=0.1, decayTime=0.2, sustainLevel=1, curve= -4,
		out=~ss.bus.master;

		var mySample, buffer, bufferFreq, rate, panFreqRange=(sampler.panFreqRange ? 1.001),
		length, envs, envTimes, sigs, sig;

		var releaseTime = \releaseTime.kr(sampler.releaseTime);

		mySample = sampler.getSample(freq);
		buffer = mySample[0];
		bufferFreq = mySample[1];

		rate = freq / bufferFreq;
		length = BufDur.ir(buffer);
		envTimes = (length / rate / 4)!3;

		sigs = 4.collect{ |i|
			PlayBuf.ar(
				numChannels: 1,
				bufnum: buffer,
				rate: BufRateScale.ir(buffer)*[rate*panFreqRange, rate/panFreqRange],
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
		sig = Mix.ar(sigs) * EnvGen.kr(
			Env.adsr(attackTime, decayTime, sustainLevel, releaseTime, curve:curve),
			gate:gate, levelScale:amp*sampler.ampScale, doneAction:2);

		Out.ar(out, sig);

	}).add;
},

)

