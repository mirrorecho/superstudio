(

title: "Sample utilities",

initModule: { arg self;
	self.makeModule("makers");
	self.makeModule("sampleData");
	// TO DO/CONSIDER add ability to search multiple paths (for work-specific makers/synths)
	self.sampleDataPath = ~ss.path ++ "ss.sampler-sampleData/sampleData.";
	self.makersPath = ~ss.path ++ "ss.sampler-makers/sampler.";

	// NOTE: the line below would NOT work because sampler module not yet added to ~ss
	// self.makeSamplerSynth("sampleDistortionPiano", "distortion", "pianoI");
},

makeSamplerSynth: {
	arg self, name, makerName, sampleDataName;
	var maker;
	self.makers[makerName.asSymbol] ? self.loadMaker(makerName);
	self.sampleData[sampleDataName.asSymbol] ? self.loadSampleData(sampleDataName);

	// NOTE:
	// self.makers[makerName.asSymbol](arg1, arg2 ... syntax throws exception
	/// meanwhile using value changes arg behavior for the function (WHY???)... as in:
	// self.makers[makerName.asSymbol].value(arg1, arg2
	// unless argument names explicitly passed
	self.makers[makerName.asSymbol].value(self:self.makers, name:name, sampleData:self.sampleData[sampleDataName.asSymbol]);
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

// basic no frills sampler
makeSampler: {

	arg self, name, sampleData;
	var myS = self.makeSamplerModule(name, sampleData);

	SynthDef(name, {
		arg amp=1.0, start=0, freq=440, out=~ss.bus.master;
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

		Out.ar(out, sig);

	}).add;

	myS;
},

loadMaker: { arg self, makerName;
	(self.makersPath ++ makerName ++ ".sc").load;
	~ss.postPretty(["Loaded sampler maker: '" ++ makerName ++ "'"]);
},

loadSampleData: { arg self, sampleDataName;
	(self.sampleDataPath ++ sampleDataName ++ ".sc").load;
	~ss.postPretty(["Loaded sample data: '" ++ sampleDataName ++ "'"]);
},


// TO CONSIDER: imlementing something like this...
/*openAllLibraries: { arg self;
	self.libraryPaths.do{|p|p.openDocument;};
},

libraryPaths: { arg self;
	(self.libraryPath ++ self.name ++ ".*.sc").pathMatch;
},*/


)
