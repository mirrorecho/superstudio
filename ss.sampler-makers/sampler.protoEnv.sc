(
// a basic sampler with an adsr envelope
title: "Proto Sampler",

name: "proto",

initModule: { arg self;
},

envArgs: (
	attackTime:0.01,
	decayTime:0.2,
	sustainLevel:0.6,
	releaseTime:1,
	peakLevel:0.8,
	curve:-4,
),

getEnv: { arg self, args=();
/*	Env.asr(
		attackTime: args.attachTime ? self.envArgs.attackTime,
		sustainLevel: args.sustainLevel ? self.envArgs.sustainLevel,
		decayTime: args.decayTime ? self.envArgs.decayTime,
		releaseTime: args.releaseTime ? self.envArgs.releaseTime,
		peakLevel: args.peakLevel ? self.envArgs.peakLevel,
		curve: args.curve ? self.envArgs.curve,
	);*/
},

plotEnv: { arg self, args=();
	self.getEnv(args).plot;
},

makeSynth: { arg self, name, sampleData, channels=2;

	self.postln;
	name.postln;
	sampleData.postln;
	"--------------------".postln;

	// var myS = ~ss.sampler.makeSamplerModule(name, sampleData);

/*	SynthDef(name, {
		arg amp=1.0, start=0, freq=440, gate=1,
		attackTime=self.envArgs.attackTime,
		// decayTime=self.envArgs.decayTime,
		sustainLevel=self.envArgs.sustainLevel,
		releaseTime=self.envArgs.releaseTime,
		curve=self.envArgs.curve,
		out=~ss.bus.master;

		var mySample, buffer, buffer_freq, rate, sig, env;

		mySample = myS.getSample(freq);
		buffer = mySample[0];
		buffer_freq=mySample[1];

		rate = freq / buffer_freq;
		sig = PlayBuf.ar(
			channels:channels,
			bufnum:buffer,
			rate:BufRateScale.kr(buffer)*rate,
			startPos:BufSampleRate.kr(buffer) * start,
			doneAction:2,
		);
		sig = sig * amp;

		sig = EnvGen.kr( self.getEnv((
			attackTime:attackTime,
			sustainLevel:sustainLevel,
			releaseTime:releaseTime,
			curve:curve,
		))
			gate:gate, doneAction:2);
		Out.ar(out, sig);

	}).add;*/

},

)