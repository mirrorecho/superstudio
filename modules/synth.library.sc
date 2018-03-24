(

initModule: { | self, ss |

	SynthDef("ss.spacey", {
		arg freq=440, amp=0.1, gate=1;
		var sig1, sig2, env1, env2;
		sig1 = SinOsc.ar(freq:freq, mul:amp/2);
		sig1 = sig1 + SinOsc.ar(freq:freq*2, mul:amp/3);
		env1 = EnvGen.kr(Env.adsr(0.02, 0.1, 0.4, 0.9), gate:gate);
		sig1 = sig1 * env1;


		sig2 = Pulse.ar(freq:freq, mul:amp/6);
		sig2 = RLPF.ar(sig2, LFNoise1.kr(8!2).range(freq*2, 9900), 0.2);
		env2 = EnvGen.kr(Env.adsr(0.8, 0.2, 0.4, 3), gate:gate, doneAction:2);


		sig2 = sig2 * env2;

		sig1 = sig1!2 + sig2;

		Out.ar(ss.bus.master, sig1!2);

	}).add;


	SynthDef( "ss.pop", {
		arg freq=440, gate=1, amp=1.0, slideTime = 1.0;
		var sig, sig2, env;
		freq = Lag.kr(freq, slideTime);
		sig = Resonz.ar(WhiteNoise.ar(1.98!2), freq, 0.04, 22) +
		Resonz.ar(WhiteNoise.ar(0.6!2), freq * 2, 0.01, 22);
		sig = sig * amp * 8;
		sig2 = Splay.ar(sig, spread:0.9);
		sig2 = FreeVerb2.ar(sig2[0], sig2[1], mix:0.4);
		env = EnvGen.kr(Env.perc, gate:gate, doneAction:2);
		sig2 = sig2 * env;
		Out.ar(ss.bus.master, sig2);
	}).add;

}

)