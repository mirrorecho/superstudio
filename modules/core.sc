(

~ss.loadModule(
    "core", // module name
    [], // namespace hierarchy for module
    [], // module dependencies
    "Core properties and functions", // friendly name
    { arg ss, module; // module function...

        module.postPretty = { arg env, msgs=[""];
            "-----------------------".postln;
            msgs.do {arg msg; msg.postln; };
            " ".postln;
        };

    }
);

)