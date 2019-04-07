(
// simple no-frills sampler that plays back buffer as-is
title: "Basic Sampler",

name: "basic",

makeSynthDef: { arg self, name, sampler;

	SynthDef(name, {
		arg amp=1.0, start=0, freq=440, out=~ss.bus.master;
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
		sig = sig * amp;

		Out.ar(out, sig);

	}).add;
},

)