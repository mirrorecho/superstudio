(
// a basic sampler with an adsr envelope
title: "Arrangement Tools with Patterns",


initModule: { arg self;
},

getEnv: { arg self;
},

makeSynth: { arg self;
},

)


~ss.sampler.makers.adsr = {

	arg self, name, sampleData;
	var myS = ~ss.sampler.makeSamplerModule(name, sampleData);

	SynthDef(name, {
		arg amp=1.0, start=0, freq=440, gate=1,
		attackTime=0.1, decayTime=0.2, sustainLevel=1, releaseTime=1, curve= -4,
		out=~ss.bus.master;
		var mySample, buffer, buffer_freq, rate, sig, env;

		mySample = myS.getSample(freq);
		buffer = mySample[0];
		buffer_freq=mySample[1];

		rate = freq / buffer_freq;
		sig = PlayBuf.ar(2,
			bufnum:buffer,
			rate:BufRateScale.kr(buffer)*rate,
			startPos:BufSampleRate.kr(buffer) * start,
			doneAction:2,
		);
		sig = sig * amp;

		sig = EnvGen.kr(
			Env.adsr(attackTime, decayTime, sustainLevel, releaseTime, peakLevel:amp, curve:curve),
			gate:gate, doneAction:2);

		Out.ar(out, sig);

	}).add;

	myS;
};

)