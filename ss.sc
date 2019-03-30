(

var ssPath = "".resolveRelative;

// DEFINES protoModule as an event with standard methods for any super studio module
var protoModule = (
	name: "aModule",
	title: "A super studio module",

	path: ssPath, // path for the module and/or child modules
	prefix: "", // will auto prepend prefix when loading child modules by name

	initModule: { arg self, callback={}; }, // hook for function to initialize module

	// really only needed as a hook
	getCopy: { arg self, eValues=();
		self ++ eValues;
	},

	makeCopy: { arg self, name, eValues=();
		var myModule = self ++ eValues;
		myModule.name = name;
		self.parent[name.asSymbol] = myModule;
		myModule;
	},

	getModule: { arg self, eValues=();
		var myModule = ~ss.protoModule ++ eValues;
		myModule.parent = self;
		myModule.initModule.value;
		myModule;
	},

	makeModule: { arg self, name, eValues=();
		self[name.asSymbol] = self.getModule(eValues);
		self[name.asSymbol].name = name;
		self[name.asSymbol];
	},

	getModuleList: { arg self, list=[], eValues=();
		var myModule = self.getModule(eValues);

		myModule.listSize = list.size;

		myModule.nameList = list.size.collect{|i| list[i].name.asSymbol;};

		list.do{|e| myModule[e.name.asSymbol] = e.copy;};

		myModule.byIndex = {arg myModule, index;
			myModule[myModule.nameList[index]];
		};

		myModule.list = {arg myModule; myModule.listSize.collect{|i| myModule.byIndex(i);}};

		myModule.getCopy = { arg myModule, eListValues=(), eValues=();
			var myCopy = myModule ++ eValues;

			// make a copy of all children:
			myCopy.nameList.do{|n|
				myCopy[n] = myCopy[n].getCopy(eListValues[n] ? ());
			};

			myCopy;
		};

		myModule.makeCopy = {  arg myModule, name, eListValues=(), eValues=();
			self.parent[name.asSymbol] = myModule.getCopy(eListValues, eValues);
		};
		myModule;
	},

	makeModuleList: { arg self, name, list=[], eValues=();
		self[name.asSymbol] = self.getModuleList(list, eValues);
		self[name.asSymbol].name = name;
		self[name.asSymbol];
	},

	loadModule: { arg self, name, path, prefix;
		var modulePath, moduleFullName, moduleFilePath, eModule;

		modulePath = (path ?? self.path);
		moduleFullName = (prefix ?? self.prefix) ++ name;
		moduleFilePath = modulePath ++ moduleFullName ++ ".sc";
		eModule = (moduleFilePath).load;

		moduleFilePath.postln;
		~ss.postPretty(["Loading module: '" ++ moduleFullName ++ "'"]);
		eModule.path = modulePath;
		eModule.filePath = moduleFilePath;
		self.makeModule(name, eModule);

		("Module: '" ++ moduleFullName ++ "' success!").postln;
		eModule;
	},

	load: { arg self, modules=[], callback={};
		{
			s.sync;
			modules.do { arg moduleName;
				self.loadModule(moduleName, self.path, self.name ++ ".");
				s.sync;
			};
			callback.value;
		}.forkIfNeeded;
	},


	openAll: { arg self;
		self.filePath.openDocument;
		self.subPaths.do{|p|p.openDocument;};
	},

	subPaths: { arg self;
		(self.path ++ self.prefix ++ "*.sc").pathMatch;
	},

	postMe: { arg self;
		~ss.postPretty(self.collect{|val, key| key.asString ++ ": " ++ val.asString;});
	},
);


~ss = protoModule ++ (

	name: "ss",

	prefix: "ss.",

	protoModule: protoModule,

	title: "Super Studio!",

	filePath: ssPath ++ "ss.sc",

	initialized: false,

	modules: ["bus", "synther", "master", "buf", "midi", "arrange", "sampler"],

	open: { arg self, subPath;
		(~ss.path ++ subPath).openOS;
	},

	startServer: {arg self, callback={};
		var ssRecycle;
		CmdPeriod.removeAll;
		ServerBoot.removeAll;
		ssRecycle = {
			s.freeAll; // necessary even with reboot?
			Server.all.do(Buffer.freeAll); // necessary even with reboot?
			s.newAllocators; // new allocators (numbers) for busses, buffers, etc.
			// s.killAll;
			self.initialized = true;
		};
		if ( self.initialized != true, {ServerBoot.add(ssRecycle, \default);} );
		s.reboot;
	},

	initModule: { arg self, callback={};
		CmdPeriod.removeAll;
		CmdPeriod.add({
			Server.all.do(Buffer.freeAll);
			self.load(self.modules, callback);
			// TO DO: load common synther libraries and common sampler libraries
		});
		CmdPeriod.run;
	},


	postPretty: { arg self, msgs=[""];
		"----------------------------------".postln;
		msgs.do {arg msg; msg.postln; };
	},

);

~ss.startServer;

)




