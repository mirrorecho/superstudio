(

~ss.sampler.makers.perc = {

	arg self, name, sampleData;
	var myS = ~ss.sampler.makeSamplerModule(name, sampleData);

	SynthDef(name, {
		arg amp=1.0, start=0, freq=440, attackTime=0.01, releaseTime=2, curve= -4, out=~ss.bus.master;
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

		env = Env.perc(attackTime:attackTime, releaseTime:releaseTime, level:amp, curve:curve);
		sig = sig * EnvGen.ar(env, doneAction: 2);

		Out.ar(out, sig);

	}).add;

	myS;
};

)