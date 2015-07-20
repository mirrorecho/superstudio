(
~ss.loadModule(
    "buf", // module name
    ["buf"], // namespace hierarchy for module
    "Buffer utilities", // friendly name
    { arg ss, module; // module function...

        module.libraryPath = "/home/randall/Echo/Sounds/Library/";

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

        SynthDef("ss.buf.play", {arg buffer, amp=1.0;
            var sig = PlayBuf.ar(2,
                bufnum:buffer,
                doneAction:2
                );
            sig = sig * amp;
            Out.ar(ss.bus.master, sig);
        }).add;

    }
);


)

