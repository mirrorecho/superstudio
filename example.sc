(
// RUN THIS BLOCK FIRST  (note this boots server)
// ... update the following to local path of superstudio.sc
("/Users/randallwest/code/mirrorecho/superstudio/ss.sc").loadPaths;
)



(
// RUN THIS NEXT TO LOAD COMMON MODULES + WORK-SPECIFIC STUFF
// (rerun after stopping sound with CMD-PERIOD ... )
~ss.loadCommon({
	/*
	add any additional ~ss.load, SynthDef, etc. here
	to run after common modules loaded
	EXAMPLES:
	*/

	/*
	// MIDI: simple defs for midi messaging
	~ss.load(["midi"], {
		~ss.midi.instrument = "ss.pop"; // <- sets midi instrument (default is "ss.spacey");
	});
	*/
});
)

(
p = PbindProxy.new;
p.set(*[instrument:"ss.spacey"]);
p.play;
)