(
~ss.synther.makeModule("ringer", (
	eDefaultArgs:(
		attackTime:0.001,
		decayTime:0.2,
		sustainLevel:1,
		releaseTime:2,
		curve: -4,
		overtones:[1, 2, 3, 4, 5, 6, 7, 8],
		overtoneAmps:[0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1],
		ringTimes:[0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1],
		bufnum: ~ss.buf['japan-cicadas']['0159-insects-in-kyoto'],
		rate:1,
		start:0,
		randStart:0,
		mix:1.0,
		out: ~ss.bus.master,
		centerFreq:440,
		rateScale:0, // scales rate of buffer
	),



	// factory function for synthdef based on bank of resonators using buffer as input and percussive env.
	makeRinger:{ arg self, name, eArgs=(); // args: bufnum, env, overtones, overtoneAmps, ringTimes, out
		var myArgs = self.eDefaultArgs ++ eArgs;

		SynthDef(name, {
			arg amp=1, freq=440;

			var
			bufnum = \bufnum.ir(myArgs.bufnum),
			attackTime = \attackTime.ir(myArgs.attackTime),
			releaseTime = \releaseTime.ir(myArgs.releaseTime),
			rate = \rate.ir(myArgs.rate),
			start = \start.ir(myArgs.start),
			randStart = \randStart.ir(myArgs.randStart), // randomize seconds up to this value to add to sample start
			mix = \mix.ir(myArgs.mix),
			curve = \curve.ir(myArgs.curve),
			centerFreq = \centerFreq.ir(myArgs.centerFreq),
			rateScale = \rateScale.ir(myArgs.rateScale),
			out = \out.ir(myArgs.out);

			var sig, bufsig, env;
			var specsArray = [
				myArgs.overtones*freq, // FREQS
				myArgs.overtoneAmps, // AMPLITUDES
				myArgs.ringTimes // RING TIMES
			];

			env = EnvGen.ar(Env.perc(attackTime, releaseTime, level:amp, curve:curve), doneAction:2);

			bufsig = PlayBuf.ar(2,
				bufnum:bufnum,
				rate:BufRateScale.kr(bufnum) * rate * ((freq/centerFreq)**rateScale),
				startPos:BufSampleRate.kr(bufnum) * (start + Rand(0, randStart)),
				// doneAction:2, // NOTE: better to keep synth going until end of env so that buffer can be used as impulse
			);

			sig = Klank.ar(`specsArray, bufsig) * AmpComp.kr(freq, 20, 0.2);
			sig = (sig*mix) + (bufsig*(1-mix));
			sig = sig * env;

			Out.ar(out, sig);

		}).add;
		("Added SynthDef: '" ++ name ++ "'").postln;
	},

	// factory function for synthdef based on bank of resonators using buffer as input and percussive env.
	makeDroneRinger:{ arg self, name, eArgs=(); // args: bufnum, env, overtones, overtoneAmps, ringTimes, out
		var myArgs = self.eDefaultArgs ++ eArgs;

		SynthDef(name, {
			arg amp=1, freq=440, gate=1;

			var
			bufnum = \bufnum.ir(myArgs.bufnum),
			attackTime = \attackTime.ir(myArgs.attackTime),
			decayTime = \decayTime.ir(myArgs.decayTime),
			sustainLevel = \sustainLevel.ir(myArgs.sustainLevel),
			releaseTime = \releaseTime.ir(myArgs.releaseTime),
			rate = \rate.ir(myArgs.rate),
			mix = \mix.ir(myArgs.mix),
			curve = \curve.ir(myArgs.curve),
			centerFreq = \centerFreq.ir(myArgs.centerFreq),
			rateScale = \rateScale.ir(myArgs.rateScale),
			out = \out.ir(myArgs.out);

			var specsArray = [
				myArgs.overtones*freq, // FREQS
				myArgs.overtoneAmps, // AMPLITUDES
				myArgs.ringTimes // RING TIMES
			];

			var bufsigs, bufsig, bufenvs, sig, env;

			var length = BufDur.ir(bufnum);
			var envTimes;

			rate = rate * ((freq/centerFreq)**rateScale);
			envTimes = (length / rate / 4)!3;

			bufsigs = 4.collect{ |i|
				PlayBuf.ar(
					numChannels:2,
					bufnum:bufnum,
					rate:BufRateScale.ir(bufnum)*rate,
					startPos:BufSampleRate.ir(bufnum) * length * i/4,
					loop:1);
			};

			bufenvs = [
				EnvGen.kr(Env.new(levels: [0, 0.25, 0.5, 0.25, 0], times: envTimes, curve: [6,-6,6,-6,]).circle),
				EnvGen.kr(Env.new(levels: [0.25, 0.5, 0.25, 0, 0.25], times: envTimes, curve: [-6,6,-6,6]).circle),
				EnvGen.kr(Env.new(levels: [0.5, 0.25, 0, 0.25, 0.5], times: envTimes, curve: [6,-6,6,-6]).circle),
				EnvGen.kr(Env.new(levels: [0.25, 0, 0.25, 0.5, 0.25], times: envTimes, curve: [-6,6,-6, 6,]).circle),
			];
			bufsigs = bufsigs * bufenvs;
			bufsig = Mix.ar(bufsigs);

			sig = Klank.ar(`specsArray, bufsig) * AmpComp.kr(freq, 20, 0.2);
			sig = (sig*mix) + (bufsig*(1-mix));

			sig = sig * EnvGen.kr(
				Env.adsr(attackTime, decayTime, sustainLevel, releaseTime, peakLevel:amp,
				),
				gate:gate, doneAction:2

			);

			Out.ar(out, sig);

		}).add;
		("Added SynthDef: '" ++ name ++ "'").postln;
	};
));





)


