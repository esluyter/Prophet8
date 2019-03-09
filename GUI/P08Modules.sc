P08Insignia : UserView {
  *new { |parent, bounds|
    ^super.new(parent, bounds).init2;
  }

  init2 {
    this.background_(Color.gray(0, 0.3));

    StaticText(this, Rect(52, 0, 150, 30))
      .string_("Prophet ’08")
      .stringColor_(Color.gray(0.2, 0.5))
      .font_(Font(Font.defaultSerifFace, 27, true, true));

    StaticText(this, Rect(55, 11, 150, 30))
      .string_("interface")
      .stringColor_(Color.gray(0.35, 0.6))
      .font_(Font(Font.defaultSerifFace, 8, true, true));

    StaticText(this, Rect(102, 11, 150, 30))
      .string_("by eric sluyter")
      .stringColor_(Color.gray(0.35, 0.6))
      .font_(Font(Font.defaultSerifFace, 8, true, true));
  }
}

P08TopPanel : P08Module {
  var <bpm, <clockDivide, <bendRange, <unisonMode, <unisonAssign, <seqTrig, <arpMode, <arpOn, <gatedSeqOn;

  *new { |parent, bounds|
    ^super.new(parent, bounds).init2;
  }

  init2 {
    this.showBorder_(false);

    bpm = P08Knob(view, Rect(430, 80, 35, 35))
      .spec_(ControlSpec(30, 250, 'linear', 0.0, 0, ""));
    P08Label(this, Rect(417, 113, 60, 20)).string_("BPM");

    clockDivide = P08PopUpMenu(this, Rect(477, 90, 60, 20)).items_([
      "1/2",
      "1/4",
      "1/8",
      "1/8 Half Swing",
      "1/8 Full Swing",
      "1/8 Triplets",
      "1/16",
      "1/16 Half Swing",
      "1/16 Full Swing",
      "1/16 Triplets",
      "1/32",
      "1/32 Triplets",
      "1/64 Triplets"
    ]);
    P08Label(this, Rect(477, 113, 60, 20)).string_("Clock Divide");

    bendRange = P08PopUpMenu(this, Rect(33, 8, 40, 20)).items_((0..12));
    P08Label(this, Rect(18, 33, 70, 20)).string_("Bend Range");

    unisonMode = P08PopUpMenu(this, Rect(98, 8, 80, 20)).items_([
      "One Voice",
      "All Voices",
      "AllDetune1",
      "AllDetune2",
      "AllDetune3"
    ]);
    P08Label(this, Rect(103, 33, 70, 20)).string_("Unison Mode");

    unisonAssign = P08PopUpMenu(this, Rect(203, 8, 64, 20)).items_([
      "Low",
      "Low Retrig",
      "High",
      "High Retrig",
      "Last",
      "Last Retrig"
    ]);
    P08Label(this, Rect(193, 33, 84, 20)).string_("Unison Assign");

    seqTrig = P08PopUpMenu(this, Rect(293, 8, 84, 20)).items_([
      "Normal",
      "Normal No Reset",
      "No Gate",
      "No Gate No Reset",
      "Key Step"
    ]);
    P08Label(this, Rect(293, 33, 84, 20)).string_("Seq Trigger");

    arpMode = P08PopUpMenu(this, Rect(403, 8, 64, 20)).items_([
      "Up",
      "Down",
      "Up/Down",
      "Assign"
    ]);
    P08Label(this, Rect(403, 33, 64, 20)).string_("Arp Mode");

    arpOn = P08Button(this, Rect(580, 70, 15, 8));
    P08Label(this, Rect(557, 82, 60, 20)).string_("Arpeggiator");

    gatedSeqOn = P08Button(this, Rect(580, 110, 15, 8));
    P08Label(this, Rect(557, 122, 60, 20)).string_("Gated Seq");
  }
}

P08LFOModule : P08Module {
  var <index, <freq, <amt, <shape, <keySync, <dest;

  *new { |parent, bounds, index = 0|
    ^super.new(parent, bounds).init2(index);
  }

  init2 { |index|
    P08Label(this, Rect(45, 10, 60, 20)).string_("Dest")
      .stringColor_(Color.hsv(0, 0, 0.5));
    dest = P08PopUpMenu(this, Rect(90, 12, 95, 15)).items_(modDests);

    freq = P08Knob(this, Rect(20, 30, 35, 35), "LFO", "Frequency", 0, 0, 166);
    P08Label(this, Rect(8, 60, 60, 20)).string_("Frequency");
    amt = P08Knob(this, Rect(70, 30, 35, 35), "LFO", "Amount");
    P08Label(this, Rect(58, 60, 60, 20)).string_("Amount");
    shape = P08PopUpMenu(this, Rect(115, 40, 50, 15))
      .items_(["Tri", "Rev Saw", "Saw", "Square", "Random"]);
    P08Label(this, Rect(110, 60, 60, 20)).string_("Shape");
    keySync = P08Button(this, Rect(178, 45, 15, 8));
    P08Label(this, Rect(168, 60, 35, 20)).string_("Key Sync");

    this.index_(index);
  }

  index_ { |value|
    index = value;
    this.title_("LFO " ++ (index + 1).asString);
    [freq, amt].do(_.section_("LFO " ++ (index + 1).asString));
  }
}

P08CtrlModule : P08Module {
  var <dests, <amts;

  *new { |parent, bounds|
    ^super.new(parent, bounds).init2;
  }

  init2 {
    dests = nil!5;
    amts = nil!5;
    ["Mod Wheel", "Pressure", "Breath", "Velocity", "Foot Controller"].do { |str, i|
      P08Label(this, Rect(15, i * 44 + 15, 95, 15)).align_(\left).string_(str);
      dests[i] = P08PopUpMenu(this, Rect(15, i * 44 + 31, 95, 15))
        .items_(modDests);
      amts[i] = P08Knob(this, Rect(123, i * 44 + 8, 35, 35), str, "Amount", 127, 0, 254)
        .displayValueFunc_({ |value| value - 127 })
        .centered_(true);
      P08Label(this, Rect(120, i * 44 + 38, 40, 20)).string_("Amount");
    };
  }
}

P08ModModule : P08Module {
  var <source, <dest, <amt;

  *new { |parent, bounds, index = 0|
    ^super.new(parent, bounds).init2(index);
  }

  init2 { |index|
    source = P08PopUpMenu(this, Rect(80, 12, 95, 15)).items_(modSrcs);
    P08Label(this, Rect(15, 10, 55, 20))
      .string_("Source")
      .align_(\right)
      .stringColor_(Color.hsv(0, 0, 0.5));

    dest = P08PopUpMenu(this, Rect(80, 35, 95, 15)).items_(modDests);
    P08Label(this, Rect(15, 33, 55, 20))
      .string_("Destination")
      .align_(\right)
      .stringColor_(Color.hsv(0, 0, 0.5));

    amt = P08Knob(this, Rect(188, 8, 35, 35), "Mod " ++ (index + 1).asString, "Amount", 127, 0, 254)
      .centered_(true)
      .displayValueFunc_({ |value| value - 127 });
    P08Label(this, Rect(185, 38, 40, 20)).string_("Amount");

    //this.title_((index + 1).asString);
  }
}

P08Env3Module : P08Module {
  var <amt, <velAmt, <delay, <attack, <decay, <sustain, <release, <repeatOn, <dest;

  *new { |parent, bounds|
    ^super.new(parent, bounds).init2;
  }

  init2 {
    this.title_("ENVELOPE 3");

    P08Label(this, Rect(123, 10, 55, 20))
      .string_("Repeat")
      .align_(\right)
      .stringColor_(Color.hsv(0, 0, 0.5));
    repeatOn = P08Button(this, Rect(188, 15, 15, 8));

    P08Label(this, Rect(250, 10, 55, 20))
      .string_("Destination")
      .align_(\right)
      .stringColor_(Color.hsv(0, 0, 0.5));
    dest = P08PopUpMenu(this, Rect(315, 12, 95, 15))
      .items_(modDests);

    amt = P08Knob(this, Rect(25, 30, 40, 40), "Env 3", "Amount", 127, 0, 254)
      .centered_(true)
      .displayValueFunc_({ |value| value - 127 });
    P08Label(this, Rect(15, 70, 60, 20)).string_("Amount");

    velAmt = P08Knob(this, Rect(85, 30, 40, 40), "Env 3", "Velocity");
    P08Label(this, Rect(75, 70, 60, 20)).string_("Velocity");

    delay = P08Knob(this, Rect(145, 30, 40, 40), "Env 3", "Env Delay");
    P08Label(this, Rect(135, 70, 60, 20)).string_("Delay");

    attack = P08Knob(this, Rect(205, 30, 40, 40), "Env 3", "Env Attack");
    P08Label(this, Rect(195, 70, 60, 20)).string_("Attack");

    decay = P08Knob(this, Rect(265, 30, 40, 40), "Env 3", "Env Decay");
    P08Label(this, Rect(255, 70, 60, 20)).string_("Decay");

    sustain = P08Knob(this, Rect(325, 30, 40, 40), "Env 3", "Env Sustain");
    P08Label(this, Rect(315, 70, 60, 20)).string_("Sustain");

    release = P08Knob(this, Rect(385, 30, 40, 40), "Env 3", "Env Release");
    P08Label(this, Rect(375, 70, 60, 20)).string_("Release");
  }
}

P08SeqModule : P08Module {
  var tracks, <track = 0, <steps, <dest;

  *new { |parent, bounds|
    ^super.new(parent, bounds).init2;
  }

  init2 {
    this.title_("SEQUENCER");

    dest = P08PopUpMenu(this, Rect(305, 12, 95, 15)).items_(modDests);
    P08Label(this, Rect(240, 10, 55, 20))
      .string_("Destination")
      .align_(\right)
      .stringColor_(Color.hsv(0, 0, 0.5));

    steps = 8.collect { |i| P08Knob(this, Rect(i * 50 + 25, 35, 40, 40)) };

    steps = steps ++ 8.collect { |i| P08Knob(this, Rect(i * 50 + 25, 78, 40, 40)) };

    tracks = 4.collect { |i|
      P08Label(this, Rect(420, i * 25 + 23, 10, 20)).string_((i + 1).asString);
      P08Button(this, Rect(440, i * 25 + 28, 15, 8)).action_({ |view|
        tracks.do(_.value_(false));
        view.value = true;
        track = i;
      });
    };

    tracks[track].value = true;
  }
}

P08LayerModule : P08Module {
  var splitPoint, stack, split, editB;

  *new { |parent, bounds|
    ^super.new(parent, bounds).init2;
  }

  init2 {
    this.showBorder_(false);

    editB = P08Button(this, Rect(75, 15, 15, 8));
    P08Label(this, Rect(52, 27, 60, 20)).string_("Edit B");

    stack = P08Button(this, Rect(25, 15, 15, 8));
    P08Label(this, Rect(12, 33, 40, 20)).string_("A/B Stack");

    split = P08Button(this, Rect(25, 65, 15, 8));
    P08Label(this, Rect(12, 82, 40, 20)).string_("A/B Split");

    splitPoint = P08Knob(this, Rect(65, 52, 35, 35));
    P08Label(this, Rect(52, 85, 60, 20)).string_("Split Point");
  }
}

P08OscModule : P08Module {
  var <freq1, <freq2, <fine1, <fine2, <shape1, <shape2, <glide1, <glide2, <keyboardOn1,
  <keyboardOn2, <mix, <noise, <sync, <glideMode, <slop, <unisonOn;

  *new { |parent, bounds|
    ^super.new(parent, bounds).init2;
  }

  init2 {
    this.title_("OSCILLATORS");

    # freq1, fine1, shape1, glide1, mix = ["Osc 1 Freq", "Fine", "Shape/PW", "Glide", "Mix"].collect { |str, i|
      P08Label(this, Rect(i * 60 + 15, 70, 60, 20)).string_(str);
      P08Knob(this, Rect(i * 60 + 25, 30, 40, 40));
    };
    # freq2, fine2, shape2, glide2, noise = ["Osc 2 Freq", "Fine", "Shape/PW", "Glide", "Noise"].collect { |str, i|
      P08Label(this, Rect(i * 60 + 15, 165, 60, 20)).string_(str);
      P08Knob(this, Rect(i * 60 + 25, 125, 40, 40));
    };

    [fine1, fine2, mix].do(_.centered_(true));

    [freq1, fine1, shape1, glide1].do(_.section_("Osc 1"));
    [freq2, fine2, shape2, glide2].do(_.section_("Osc 2"));

    [freq1, freq2].do({ |knob|
      knob
        .spec_(ControlSpec(0, 120))
        .name_("Frequency")
        .displayValueFunc_({ |value| value.midiname })
    });
    [fine1, fine2].do { |knob|
      knob
        .spec_(ControlSpec(0, 100))
        .name_("Fine tune")
        .defaultValue_(50)
        .displayValueFunc_({ |value| value - 50 })
    };
    [shape1, shape2].do { |knob|
      knob
        .spec_(ControlSpec(0, 103))
        .name_("Shape")
        .displayValueFunc_({ |value|
          var shape;
          if (value == 0) {
            shape = "Off"
          };
          if (value == 1) {
            shape = "Sawtooth"
          };
          if (value == 2) {
            shape = "Triangle"
          };
          if (value == 3) {
            shape = "Saw/Tri"
          };
          if (value >= 4) {
            shape = "Pulse " ++ (value - 4).asString;
          };
          shape;
        })
    };
    [glide1, glide2].do(_.name_("Glide"));
    mix.section_("Osc").name_("Mix").defaultValue_(64);
    noise.section_("Noise").name_("Amount");

    sync = P08Button(this, Rect(45, 98, 15, 8));
    P08Label(this, Rect(35, 108, 35, 20)).string_("Sync");

    glideMode = P08PopUpMenu(this, Rect(80, 94, 115, 15)).items_([
      "Fixed rate",
      "Fixed rate auto",
      "Fixed time",
      "Fixed time auto"
    ]);
    P08Label(this, Rect(110, 108, 55, 20)).string_("Glide Mode");

    slop = P08PopUpMenu(this, Rect(207, 94, 35, 15)).items_(["0", "1", "2", "3", "4"]);
    P08Label(this, Rect(207, 108, 35, 20)).string_("Slop");

    unisonOn = P08Button(this, Rect(265, 98, 15, 8));
    P08Label(this, Rect(255, 108, 35, 20)).string_("Unison");


    P08Label(this, Rect(175, 10, 100, 20))
      .stringColor_(Color.hsv(0, 0, 0.5))
      .align_(\left)
      .string_("Keyboard On");

    keyboardOn1 = P08Button(this, Rect(258, 15, 15, 8));
    P08Label(this, Rect(245, 10, 10, 20)).string_("1");

    keyboardOn2 = P08Button(this, Rect(295, 15, 15, 8));
    P08Label(this, Rect(282, 10, 10, 20)).string_("2");
  }
}

P08FilterModule : P08Module {
  var <freq, <res, <envAmt, <velAmt, <keyboardAmt, <mod, <delay, <attack, <decay,
  <sustain, <release, <poles;

  *new { |parent, bounds|
    ^super.new(parent, bounds).init2;
  }

  init2 {
    this.title_("LOWPASS FILTER");

    # freq, res, envAmt, velAmt, keyboardAmt, mod = ["Frequency", "Resonance", "Env Amount", "Velocity", "Key Amount", "Audio Mod"].collect { |str, i|
      P08Label(this, Rect(i * 62 + 10, 70, 60, 20)).string_(str);
      P08Knob(this, Rect(i * 60 + 25, 30, 40, 40), "Lowpass", str);
    };
    freq.spec_(ControlSpec(0, 164));
    velAmt.name_("Env Velocity");
    keyboardAmt.name_("Keyboard Amt");
    envAmt
      .name_("Envelope Amt")
      .spec_(ControlSpec(0, 254))
      .defaultValue_(127)
      .displayValueFunc_({ |value| value - 127 })
      .centered_(true);

    poles = P08Button(this, Rect(35, 114, 15, 8));
    P08Label(this, Rect(25, 124, 35, 20)).string_("4 Pole");

    # delay, attack, decay, sustain, release = ["Delay", "Attack", "Decay", "Sustain", "Release"].collect { |str, i|
      P08Label(this, Rect(i * 60 + 75, 135, 60, 20)).string_(str);
      P08Knob(this, Rect(i * 60 + 85, 95, 40, 40), "Lowpass", "Env " ++ str);
    };
  }
}

P08AmpModule : P08Module {
  var <initLevel, <envAmt, <velAmt, <spread, <volume, <delay, <attack, <decay, <sustain, <release;

  *new { |parent, bounds|
    ^super.new(parent, bounds).init2;
  }

  init2 {
    this.title_("AMP");

    # initLevel, envAmt, velAmt, spread, volume = ["VCA Level", "Env Amount", "Velocity", "Pan Spread", "Volume"].collect { |str, i|
      P08Label(this, Rect(i * 62 + 10, 70, 60, 20)).string_(str);
      P08Knob(this, Rect(i * 60 + 25, 30, 40, 40), "VCA");
    };
    initLevel.name_("Init Level");
    envAmt.name_("Envelope Amt");
    velAmt.name_("Env Velocity");
    spread.name_("Pan Spread");
    volume.name_("Voice Volume");

    # delay, attack, decay, sustain, release = ["Delay", "Attack", "Decay", "Sustain", "Release"].collect { |str, i|
      P08Label(this, Rect(i * 60 + 15, 135, 60, 20)).string_(str);
      P08Knob(this, Rect(i * 60 + 25, 95, 40, 40), "VCA", "Env " ++ str);
    };
  }
}

P08LCDView : UserView {
  var lines;
  var program = 6, bank = 1, name = "Bridge Seq";
  var rout;

  *new { |parent, bounds|
    ^super.new(parent, bounds).init;
  }

  init {
    this.background_(Color.black);

    lines = [
      StaticText(this, Rect(9, 4, 195, 30))
        .font_(Font(Font.defaultMonoFace, 18))
        .stringColor_(Color.hsv(0, 0.6, 0.7)),
      StaticText(this, Rect(9, 33, 195, 30))
        .font_(Font(Font.defaultMonoFace, 18))
        .stringColor_(Color.hsv(0, 0.6, 0.7))
    ];

    this.showDefaultMessage;
  }

  showValue { |section = "", parameter = "", defaultValue = 0, value = 0, dur = 3|
    var line1, line2;
    var spaces;
    defaultValue = defaultValue.asString;
    value = value.asString;
    spaces = 17 - section.size;
    line1 = "%%".format(section, defaultValue.padLeft(spaces));
    parameter = parameter ++ ":";
    spaces = 17 - parameter.size;
    line2 = "%%".format(parameter, value.padLeft(spaces));
    this.showMessage(line1, line2, dur);
  }

  showMessage { |line1, line2, dur = 3|
    lines[0].string_(line1);
    lines[1].string_(line2);
    rout.stop;
    rout = {
      dur.wait;
      this.showDefaultMessage;
    }.fork(AppClock);
  }

  showDefaultMessage {
    lines[0].string_("Program% Bank %".format(program.asPaddedString(3, " "), bank));
    lines[1].string_(name);
  }
}
