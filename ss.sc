/*
TO DO!
 - more cool synths
 - - - drone maker
 - - - swish, swell
 - - - swish effects
 - - - ghost sound
 - - - simple drums
 - tempo clock module?
*/
~ss[]

[] ?? "DFDFD";

(
var myPath = "".resolveRelative;
var eProtoModule =

~ss = (

	initialized: false, // ?? necessary?

	path: myPath,

	projectPath: myPath, // will typically replace with project specific path

	start: {arg ss, callback={};
		var ssRecycle = {
			s.freeAll;
			Server.all.do(Buffer.freeAll); // necessary even with reboot?
			s.newAllocators; // new allocators (numbers) for busses, buffers, etc.
			// s.killAll;
			ss.initialized = true;
		};
		if ( ss.initialized != true, {ServerBoot.add(ssRecycle, \default);} );
		s.reboot;
	},

	eProtoModule: (
		name: "YO?",
		nameSpace: [],
		title: "A super studio module",
		initModule: { | self, ss | }, // hook for function to initialize module
		load: { | self | },
	),

	makeModule: { arg ss, moduleName, eModule=();
		var parentModule = ss;
		var eProtoCopy = ().putAll(ss.eProtoModule);
		var eModuleCombo = eProtoCopy.putAll(eModule);
		eModuleCombo.nameSpace.do { | nameSpace |
		}
		ss[moduleName.asSymbol] = eModuleCombo;
		ss[moduleName.asSymbol].ss = ss;
		ss[moduleName.asSymbol].initModule(ss);
	},

	load: { arg ss, modules=[], callback={}, path;
		var eModule;
		{
			s.sync;
			modules.do { arg moduleName;
				(path ?? (ss.path ++ "modules/") ++ moduleName ++ ".sc").postln;
				eModule = (path ?? (ss.path ++ "modules/") ++ moduleName ++ ".sc").load;
				ss.makeModule(moduleName, eModule);
				s.sync;
				("Loaded module: '" ++ moduleName ++ "'").postln;
			};
			callback.value;

		}.fork;
	},

	loadLocal: { arg ss, modules=[], callback={};
		ss.load(modules, callback, ss.projectPath);
	},

	loadCommon: { arg ss, callback={};
		ss.load(["bus","master","synth.library","buf"], callback);
	},


	postPretty: { arg ss, msgs=[""];
		"-----------------------".postln;
		msgs.do {arg msg; msg.postln; };
		" ".postln;
	},

);

~ss.start;

)
