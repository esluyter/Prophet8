P08Knob : Knob {
  var <>spec;
  var <defaultValue;
  var <>section;
  var <>name;
  var <>lcdView;
  var <>displayValueFunc;

  *new { |parent, bounds, section = "Section", name = "Knob", defaultValue = 0, minValue = 0, maxValue = 127|
    ^super.new(parent, bounds).init(section, name, defaultValue, minValue, maxValue);
  }

  init { |argSection, argName, argDefaultValue, minValue, maxValue|
    name = argName;
    section = argSection;
    spec = ControlSpec(minValue, maxValue, 'linear', 0.0, 0, "");
    displayValueFunc = { |value| value };
    this.defaultValue_(argDefaultValue);
    this.color_([Color.black, Color.white, Color.clear, Color.white]);
    this.action_({
      this.displayValueOnLCD;
      //this.changed;
    });
    this.mouseDownAction_({ |view, x, y, mod, buttNum, clickCount|
      if (buttNum == 0 && (clickCount == 2)) {
        this.valueAction_(this.defaultValue)
      };
    });
    this.mode_(\vert);
  }

  displayValueOnLCD {
    if (lcdView.notNil) {
      lcdView.showValue(
        section, name,
        displayValueFunc.(this.defaultValue), displayValueFunc.(this.value)
      );
    };
  }

  value_ { |val|
    super.value_(spec.unmap(val));
    this.displayValueOnLCD;
  }

  defaultValue_ { |value|
    defaultValue = value;
    this.value_(value);
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
