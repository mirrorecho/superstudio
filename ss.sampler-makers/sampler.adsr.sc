(
// simple sampler with ADSR envelope
title: "ADSR Sampler",

initModule: { arg self;
},

name: "basic",

makeSynthDef: { arg self, name, sampler;

	SynthDef(name, {
		arg amp=1.0, start=0, freq=440, gate=1,
		attackTime=0.1, decayTime=0.2, sustainLevel=1, releaseTime=1, curve= -4,
		out=~ss.bus.master;
		var mySample, buffer, bufferFreq, rate, sig;

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
		sig = sig * EnvGen.kr(
			Env.adsr(attackTime, decayTime, sustainLevel, releaseTime, curve:curve),
			gate:gate, levelScale:amp, doneAction:2);

		Out.ar(out, sig);

	}).add;
},

)