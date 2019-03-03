(
// SIMPLE WHITE NOISE WITH BANDPASS FILTER AND PERCUSSIVE ENVELOPE
SynthDef("noiseHitI", { arg freq=440, amp=0.4, rq=0.2316, // this is aprox BW of 1/3 octave, see: https://www.rane.com/note170.html
	attackTime=0.01, releaseTime=0.5, curve= -4,
	gate=1, out= ~ss.bus.master;

	var sig, env;

	sig = BrownNoise.ar(2!2) * (1/(rq**0.66));

	sig = BPF.ar(sig, freq, rq);

	env = EnvGen.kr(Env.perc(attackTime, releaseTime, amp, curve), gate:gate, doneAction:2);

	Out.ar(out, sig*env);

}).add;


)

