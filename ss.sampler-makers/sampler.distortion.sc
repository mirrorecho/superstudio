(

~ss.sampler.makers.distortion = {

	arg self, name, sampleData;
	var myS = ~ss.sampler.makeSamplerModule(name, sampleData);
	SynthDef(name, {
		arg amp=1.0, start=0, freq=440, attackTime=0.001, releaseTime=4, curve= -1, distortion=0,
		out=~ss.bus.master;
		var mySample, buffer, buffer_freq, rate, sig, env, lpf_freq, lpf_attempt_freq, lpf_cutoff_freq,
		sig_distort;

		mySample = myS.getSample(freq);
		buffer = mySample[0];
		buffer_freq=mySample[1];
		lpf_cutoff_freq = 20000;

		rate = freq / buffer_freq;
		sig = PlayBuf.ar(2,
			bufnum:buffer,
			rate:BufRateScale.kr(buffer)*rate,
			startPos:BufSampleRate.kr(buffer) * start,
			doneAction:2,
		);

		sig_distort = (sig * (3 + (distortion * 40))).distort * (1-(distortion/1.4)) * 0.4;
		sig = (sig * (1-distortion)) + (sig_distort * distortion);
		env = Env.perc(attackTime:attackTime, releaseTime:releaseTime, level:amp, curve:curve);
		sig = sig * EnvGen.ar(env, doneAction: 2);
		sig = sig * amp;

		Out.ar(out, sig);

	}).add;

	myS;
};

)