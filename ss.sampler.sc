(

title: "Sample utilities",

// run ifconfig | grep inet to get IP address to send to

initModule: { arg self;
	self.makeModule("makers",(
		path: ~ss.path ++ "ss.sampler-makers/",
		prefix: "sampler.",
	));
	self.makeModule("sampleData",(
		path: ~ss.path ++ "ss.sampler-sampleData/",
		prefix: "sampleData.",
	));

	// NOTE: the line below would NOT work here because sampler module is not yet added to ~ss when initModule called
	// self.makeSampler("sampleDistortionPiano", "distortion", "pianoI");
},

samplerDefaults: (
	channels: 2, // NOTE: event already has an attribute for numChannels, so naming this simply "channels" to avoid conflict
	releaseTime: 2, // for samplers with ADSR or Perc Envelope
	ampScale: 1, // or samplers that need across the board amp adjustment (e.g. drones)
),

makeSampler: {
	arg self, name, makerName, sampleDataName, samplerArgs=();
	var myMaker = self.makers.loadModule(makerName);
	var mySampleData = self.sampleData.loadModule(sampleDataName);

	var myS = self.makeModule(name, (sampleData:mySampleData, maker:myMaker) ++ self.samplerDefaults ++ samplerArgs);

	// TO DO...  could use pairsDo to simplify this...

	// adds the cutover frequencies as third element to each array, if doesn't already exist (except for the last array);
	(mySampleData.data.size).do{ |i|
		var mySample = mySampleData.data[i];
		mySample[0] = mySample[0].bufnum;
		if (mySample.size < 3 && (i+1) < mySampleData.data.size, {
			mySample.add(mySample[1] + ((mySampleData.data[i+1][1] - mySample[1]) / 2) );
		});
	};

	// TO DO? ...could use Select to simply the below?

	// gets the appropriate sample data element based on frequency
	myS.getSample = {arg myS, freq;
		var mySample = myS.sampleData.data[0] * (freq < myS.sampleData.data[0][2]);
		(myS.sampleData.data.size-2).do{ |i|
				mySample = mySample + (myS.sampleData.data[i+1] * (freq >= myS.sampleData.data[i][2]) * (freq < myS.sampleData.data[i+1][2]));
			};
		mySample = mySample + (myS.sampleData.data[myS.sampleData.data.size-2] * (freq >= myS.sampleData.data[myS.sampleData.data.size-2][2]));
	};

	myMaker.makeSynthDef(name, myS);

	myS;

},


// TO DO: DRY!!!!!!
makeSamplerFromBufAndFreq: {
	arg self, name, makerName, sampleBuf, sampleFreq=440,  samplerArgs=();
	var myMaker = self.makers.loadModule(makerName);
	var mySampleData = (
		name: name,
		// on I string tuned to B
		data: [
			[sampleBuf, sampleFreq],
			[sampleBuf, sampleFreq],
		],
	);

	var myS = self.makeModule(name, (sampleData:mySampleData, maker:myMaker) ++ self.samplerDefaults ++ samplerArgs);

	// TO DO...  could use pairsDo to simplify this...

	// adds the cutover frequencies as third element to each array, if doesn't already exist (except for the last array);
	(mySampleData.data.size).do{ |i|
		var mySample = mySampleData.data[i];
		mySample[0] = mySample[0].bufnum;
		if (mySample.size < 3 && (i+1) < mySampleData.data.size, {
			mySample.add(mySample[1] + ((mySampleData.data[i+1][1] - mySample[1]) / 2) );
		});
	};

	// gets the appropriate sample data element based on frequency
	myS.getSample = {arg myS, freq;
		var mySample = myS.sampleData.data[0] * (freq < myS.sampleData.data[0][2]);
		(myS.sampleData.data.size-2).do{ |i|
				mySample = mySample + (myS.sampleData.data[i+1] * (freq >= myS.sampleData.data[i][2]) * (freq < myS.sampleData.data[i+1][2]));
			};
		mySample = mySample + (myS.sampleData.data[myS.sampleData.data.size-2] * (freq >= myS.sampleData.data[myS.sampleData.data.size-2][2]));
	};

	myMaker.makeSynthDef(name, myS);

	myS;

},


)

sampler
