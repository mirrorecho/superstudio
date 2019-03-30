(

title: "Sample utilities",

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
	// self.makeSamplerSynth("sampleDistortionPiano", "distortion", "pianoI");
},

makeSampler: {
	arg self, name, makerName, sampleDataName, samplerArgs=(channels:2);
	var myMaker = self.makers[makerName.asSymbol] ? self.makers.loadModule(makerName);
	var mySampleData = self.sampleData[sampleDataName.asSymbol] ? self.sampleData.loadModule(sampleDataName);

	var myS = self.makeModule(name, (sampleData:mySampleData, maker:myMaker) ++ samplerArgs);

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

	// NOTE:
	// self.makers[makerName.asSymbol](arg1, arg2 ... syntax throws exception
	/// meanwhile using value changes arg behavior for the function (WHY???)... as in:
	// self.makers[makerName.asSymbol].value(arg1, arg2
	// unless argument names explicitly passed
	// self.makers[makerName.asSymbol].value(self:self.makers, name:name, sampleData:self.sampleData[sampleDataName.asSymbol]);
},

//
// makeSamplerModule: {
// 	arg self, name, sampleData; // sampleData should be an array 2 or 3-element arrays, each with sample and frequency
// 	var myS = self.makeModule(name, (sampleData:sampleData));
//
// 	// TO DO...  could use pairsDo to simplify this
//
// 	// adds the cutover frequencies as third element to each array, if doesn't already exist (except for the last array);
// 	(myS.sampleData.size).do{ |i|
// 		var mySample = myS.sampleData[i];
// 		mySample[0] = mySample[0].bufnum;
// 		if (mySample.size < 3 && (i+1) < myS.sampleData.size, {
// 			mySample.add(mySample[1] + ((myS.sampleData[i+1][1] - mySample[1]) / 2) );
// 		});
// 	};
//
// 	// gets the appropriate sample data element based on frequency
// 	myS.getSample = {arg myS, freq;
// 		var mySample = myS.sampleData[0] * (freq < myS.sampleData[0][2]);
// 		(myS.sampleData.size-2).do{ |i|
// 			mySample = mySample + (myS.sampleData[i+1] * (freq >= myS.sampleData[i][2]) * (freq < myS.sampleData[i+1][2]));
// 		};
// 		mySample = mySample + (myS.sampleData[myS.sampleData.size-2] * (freq >= myS.sampleData[sampleData.size-2][2]));
// 	};
// 	myS;
//
// },


// TO CONSIDER: implementing something like this...
/*
openAllLibraries: { arg self;
	self.libraryPaths.do{|p|p.openDocument;};
},

libraryPaths: { arg self;
	(self.libraryPath ++ self.name ++ ".*.sc").pathMatch;
},
*/


)
