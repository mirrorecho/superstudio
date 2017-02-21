(
~ss.makeModule(
    "buf", // module name
    ["buf"], // namespace hierarchy for module
    "Buffer utilities", // friendly name
    { arg ss, module; // module function...

        module.libraryPath = "/";

        module.loadLibrary = {arg env, library;
            var postMsgs = [];
            module[library.asSymbol] = Environment.make;
            SoundFile.collectIntoBuffers(module.libraryPath ++ library ++ "/*").do { arg buffer;
                var bufferName = buffer.path.basename.splitext[0];
                module[library.asSymbol][bufferName.asSymbol] = buffer;
                postMsgs = postMsgs.add("Loaded buffer: ~ss.buf['" ++ library ++ "']['" ++ bufferName ++ "']");
            };
            ss.postPretty(postMsgs);
        };

        SynthDef("ss.buf.play", {arg buffer, amp=1.0, rate=1.0;
            var sig = PlayBuf.ar(2,
                bufnum:buffer,
                rate:BufRateScale.kr(buffer)*rate,
                doneAction:2,
                );
            sig = sig * amp;
            Out.ar(ss.bus.master, sig);
        }).add;

		SynthDef("ss.buf.drone",{ arg buffer, amp=1.0, rate=1.0,
			// TO DO: implement these:
			startOn=0, endOn=3,
			fadeIn=0.1, sustain=1.0, fadeOut=0.1;

			var length = endOn - startOn;
			var mul = amp; // could do adjustments here...

			var myPlayBuf = PlayBuf.ar(
				numChannels:2,
				bufnum:buffer,
				rate:BufRateScale.kr(buffer)*rate,
				loop:1) * EnvGen.ar(Env.circle([0,1,0], [length/(2*rate), length/(2*rate), 0]));
			Out.ar(~ss.bus.master,
				// dividing by rate is important to adjust circle to any possible rate...
				DelayN.ar(myPlayBuf, length/(2*rate), length/(2*rate), 1, myPlayBuf)
				* mul
				,0.0);
		}).add;

		module.makeSynth = { arg env, synthName, libraryName, bufferName, args=[];
			var buffer, mySynth;
			if (module.includesKey(libraryName.asSymbol), {
				if (module[libraryName.asSymbol].includesKey(bufferName.asSymbol), {
					buffer = module[libraryName.asSymbol][bufferName.asSymbol];
					mySynth = Synth(synthName, [buffer:buffer]++args);
				}, {
					ss.postPretty(["ERROR: cannot play buffer \"" ++ bufferName ++ "\" because it does not exist in the library \"" ++ libraryName ++ "\"."]);
				});
			}, {
				ss.postPretty(["ERROR: cannot play buffer in library \"" ++ libraryName ++ "\" because the library has not been loaded."]);
			});
			mySynth;
		};

		module.play = { arg env, libraryName, bufferName, args=[];
			module.makeSynth("ss.buf.play", libraryName, bufferName, args);
		};

		module.drone = { arg env, libraryName, bufferName, args=[];
			module.makeSynth("ss.buf.drone", libraryName, bufferName, args);
		};

    }
);


)

