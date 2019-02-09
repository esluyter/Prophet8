P08Knob : Knob {
  var <>spec;

  *new { |parent, bounds|
    ^super.new(parent, bounds).init;
  }

  init {
    spec = ControlSpec(0, 127, 'linear', 0.0, 0, "");
    this.color_([Color.black, Color.clear, Color.clear, Color.white]);
  }

  value_ { |val|
    super.value_(spec.unmap(val));
  }

  value {
    ^spec.map(super.value).asInt;
  }
}

P08PopUpMenu : PopUpMenu {
  *new { |parent, bounds|
    ^super.new(parent, bounds).init;
  }

  init {
    this.font_(Font(Font.defaultMonoFace, 9))
      .background_(Color.gray(0))
      .stringColor_(Color.hsv(0, 0.6, 0.7));
  }
}

P08Label : StaticText {
  *new { |parent, bounds|
    ^super.new(parent, bounds).init;
  }

  init {
    this.stringColor_(Color.hsv(0, 0, 0.75))
      .align_(\center)
      .font_(Font("Helvetica", 10, true));
  }
}

P08Button : SCViewHolder {
  var <value = false, <action;

  *new { |parent, bounds|
    ^super.new.init(parent, bounds);
  }

  init { |parent, bounds|
    bounds = bounds ?? Rect(0, 0, parent.bounds.width, parent.bounds.height);
    view = UserView(parent, bounds)
      .drawFunc_({ |view|
        Pen.addRoundedRect(Rect(0, 0, view.bounds.width, view.bounds.height), view.bounds.width / 2, view.bounds.height / 2);
        if (value) {
          Pen.fillColor = Color.hsv(0, 0.8, 1);
          Pen.strokeColor = Color.gray(1, 0.5);
          Pen.width = 2;
          Pen.draw(3);
        } {
          Pen.color = Color.gray(0.5);
          Pen.fill;
        }
      })
      .mouseDownAction_({ |view|
        value = value.not;
        action.(this);
        this.refresh
      });
  }

  value_ { |val|
    value = val;
    if (val.isInteger) {
      value = val != 0;
    };
    this.refresh;
  }

  action_ { |value|
    action = value;
  }
}
