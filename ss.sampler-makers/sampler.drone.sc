(

~ss.sampler.makers.drone = {

	arg self, name, sampleData;
	var myS = ~ss.sampler.makeSamplerModule(name, sampleData);

	SynthDef(name, {
		arg amp=1.0, freq=440, gate=1,
		attackTime=0.1, decayTime=0.2, sustainLevel=1, releaseTime=1, curve= -4,
		out=~ss.bus.master;
		var mySample, bufnum, buffer_freq, rate, length, envTimes,
		sigs, sig, envs, env;

		mySample = myS.getSample(freq);
		bufnum = mySample[0];
		buffer_freq=mySample[1];
		rate = freq / buffer_freq;

		length = BufDur.ir(bufnum);
		envTimes = (length / rate / 4)!3;

		sigs = 4.collect{ |i|
			PlayBuf.ar(
				numChannels:2,
				bufnum:bufnum,
				rate:BufRateScale.ir(bufnum)*rate,
				startPos:BufSampleRate.ir(bufnum) * length * i/4,
				loop:1);
		};

		envs = [
			EnvGen.kr(Env.new(levels: [0, 0.25, 0.5, 0.25, 0], times: envTimes, curve: [6,-6,6,-6,]).circle),
			EnvGen.kr(Env.new(levels: [0.25, 0.5, 0.25, 0, 0.25], times: envTimes, curve: [-6,6,-6,6]).circle),
			EnvGen.kr(Env.new(levels: [0.5, 0.25, 0, 0.25, 0.5], times: envTimes, curve: [6,-6,6,-6]).circle),
			EnvGen.kr(Env.new(levels: [0.25, 0, 0.25, 0.5, 0.25], times: envTimes, curve: [-6,6,-6, 6,]).circle),
		];
		sigs = sigs * envs;

		sig = Mix.ar(sigs) * EnvGen.kr(
			Env.adsr(attackTime, decayTime, sustainLevel, releaseTime, peakLevel:amp, curve:curve),
			gate:gate, doneAction:2);

		Out.ar(out, sig);

	}).add;

	myS;
};

)