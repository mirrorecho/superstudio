(
~yo = Environment.make;
~yo.know = true;
~yo.yo1 = { arg yo;
	{
		// s.reboot;
		s.sync;
		postln("HELLO DOLLY");
		yo.yo2;
	}.fork;
};
~yo.yo2 = { arg yo;
	{
		1.wait;
		postln("HELLO SHEEP!!");
	}.fork;
};

)


~yo.yo1;