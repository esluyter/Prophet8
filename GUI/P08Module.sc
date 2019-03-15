P08Module : SCViewHolder {
  var <title = "";
  var <showBorder = true;
  var titleView;
  classvar modSrcs, modDests;

  *initClass {
    modSrcs = [
      "Off",
      "Sequence 1",
      "Sequence 2",
      "Sequence 3",
      "Sequence 4",
      "LFO 1",
      "LFO 2",
      "LFO 3",
      "LFO 4",
      "Filter Envelope",
      "VCA Envelope",
      "Envelope 3",
      "Pitch Bend",
      "Mod Wheel",
      "Pressure",
      "MIDI Breath",
      "MIDI Foot",
      "MIDI Expression",
      "Velocity",
      "Note Number",
      "Noise"
    ];
    modDests = [
      "Off",
      "Osc 1 Freq",
      "Osc 2 Freq",
      "Osc 1&2 Freq",
      "Osc Mix",
      "Noise Level",
      "Osc 1 PW",
      "Osc 2 PW",
      "Osc 1&2 PW",
      "Filter Freq",
      "Resonance",
      "Audio Mod Amt",
      "Vca Level",
      "Pan Spread",
      "LFO 1 Freq",
      "LFO 2 Freq",
      "LFO 3 Freq",
      "LFO 4 Freq",
      "All LFO freq",
      "LFO 1 Amt",
      "LFO 2 Amt",
      "LFO 3 Amt",
      "LFO 4 Amt",
      "All LFO Amt",
      "Filter Env Amt",
      "Amp Env Amt",
      "Env 3 Amt",
      "All Env Amounts",
      "Env 1 Attack",
      "Env 2 Attack",
      "Env 3 Attack",
      "All Env Attacks",
      "Env 1 Decay",
      "Env 2 Decay",
      "Env 3 Decay",
      "All Env Decays",
      "Env 1 Release",
      "Env 2 Release",
      "Env 3 Release",
      "All Env Releases",
      "Mod 1 Amt",
      "Mod 2 Amt",
      "Mod 3 Amt",
      "Mod 4 Amt"
    ];
  }

  *new { |parent, bounds|
    ^super.new.init(parent, bounds);
  }

  init { |parent, bounds|
    bounds = bounds ?? Rect(0, 0, parent.bounds.width, parent.bounds.height);
    view = UserView(parent, bounds)
      .drawFunc_({ |view|
        if (showBorder) {
          Pen.color = Color.hsv(0, 0, 0.75);
          Pen.addRoundedRect(Rect(5, 5, view.bounds.width - 10, view.bounds.height - 10), 8, 8);
          Pen.width = 2;
          Pen.stroke;
        };
      });
    titleView = StaticText(view, Rect(20, 10, 100, 20))
      .stringColor_(Color.hsv(0, 0, 0.75))
      .string_(title)
      .font_(Font("Helvetica", 10, true));
  }

  title_ { |value|
    title = value;
    titleView.string_(title);
  }

  showBorder_ { |value|
    showBorder = value;
    view.refresh;
  }

  lcdView_ { |value|
    this.children.do { |child|
      if (child.respondsTo('lcdView_')) {
        child.lcdView_(value);
      };
    };
  }

  prRegisterDependant { |object|
    this.children.do { |child|
      child.postln;
      child.addDependant(object);
    };
  }
}
