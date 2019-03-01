
(
~ghosty = ~ss.synther.makeModule("ghosty", (
	initModule: {},

	eMoanDefaultArgs: (
		oscType:SinOsc,
		freqMul:1 + (1/8),
		moanRate:4,
		panRate:3,
		panMul:0.8
	),

	makeMoan: { arg self, name, eArgs=();
		var myArgs = ().putAll(self.eMoanDefaultArgs).putAll(eArgs);

		SynthDef(name, { arg out=~ss.bus.master, amp=0.2, gate=1, freq=440;
			var sig, env;
			var freqMul = \freqMul.kr(myArgs.freqMul, 0.5);
			var lf=LFNoise1.kr(freq:myArgs.moanRate);
			var moanFreq = freq*(freqMul**lf);

			sig = Pan2.ar(
				in:myArgs.oscType.ar(freq:moanFreq, mul:amp*0.6),
				pos:LFNoise2.kr(myArgs.panRate, mul:myArgs.panMul),
			);

			env = EnvGen.kr(Env.adsr(attackTime:0.1, sustainLevel:1.0, releaseTime:0.5), gate:gate, doneAction:2);

			Out.ar(out, sig*env);

		}).add;
	},
));

~ghosty.makeMoan("ghostyMoan");
~ghosty.makeMoan("ghostyBigMoan", (oscType:Saw, freqMul:2, moanRate:8, panRate:1, panMul:0.6));

)

~ss.midi.synthName="ghostyBigMoan"

