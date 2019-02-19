(

title: "Sample utilities",

initModule: { arg self;
},

makeSamplerModule: {
	arg self, name, sampleData; // sampleData should be an array 2 or 3-element arrays, each with sample and frequency
	var myS = self.makeModule(name, (sampleData:sampleData));

	// TO DO...  could use pairsDo to simplify this

	// adds the cutover frequencies as third element to each array, if doesn't already exist (except for the last array);
	(myS.sampleData.size).do{ |i|
		var mySample = myS.sampleData[i];
		mySample[0] = mySample[0].bufnum;
		if (mySample.size < 3 && (i+1) < myS.sampleData.size, {
			mySample.add(mySample[1] + ((myS.sampleData[i+1][1] - mySample[1]) / 2) );
		});
	};

	// gets the appropriate sample data element based on frequency
	myS.getSample = {arg myS, freq;
		var mySample = myS.sampleData[0] * (freq < myS.sampleData[0][2]);
		(myS.sampleData.size-2).do{ |i|
				mySample = mySample + (myS.sampleData[i+1] * (freq >= myS.sampleData[i][2]) * (freq < myS.sampleData[i+1][2]));
			};
		mySample = mySample + (myS.sampleData[myS.sampleData.size-2] * (freq >= myS.sampleData[sampleData.size-2][2]));
	};
	myS;

},


makeSampler: {

	arg self, name, sampleData;
	var myS = self.makeSamplerModule(name, sampleData);

	SynthDef(name, {
		arg amp=1.0, start=0, freq=440, bus=~ss.bus.master;
		var mySample, buffer, buffer_freq, rate, sig;

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

		Out.ar(bus, sig);

	}).add;

	myS;
},


makePercSampler: {

	arg self, name, sampleData;
	var myS = self.makeSamplerModule(name, sampleData);

	SynthDef(name, {
		arg amp=1.0, start=0, freq=440, attackTime=0.01, releaseTime=2, curve= -4, bus=~ss.bus.master;
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

		Out.ar(bus, sig);

	}).add;

	myS;
},


makeDistortionSampler: {

	arg self, name, sampleData;
	var myS = self.makeSamplerModule(name, sampleData);

	SynthDef(name, {
		arg amp=1.0, start=0, freq=440, attackTime=0.001, releaseTime=4, curve= -1, distortion=0,
		bus=~ss.bus.master;
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

		sig  = sig * AmpComp.kr(freq, 400, 0.2);

		sig_distort = (sig * (3 + (distortion * 40))).distort * (1-(distortion/1.4)) * 0.4;
		sig = (sig * (1-distortion)) + (sig_distort * distortion);
		env = Env.perc(attackTime:attackTime, releaseTime:releaseTime, level:amp, curve:curve);
		sig = sig * EnvGen.ar(env, doneAction: 2);
		sig = sig * amp;

		Out.ar(bus, sig);

	}).add;

	myS;
},




)
