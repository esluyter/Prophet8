P08GUIController {
  var <model, <bounds, <window, <view;

  *new { |model, bounds = (Rect(0, 0, 1080, 530))|
    ^super.new.init(model, bounds).makeWindow.refreshViewDefaults;
  }

  init { |argModel, argBounds|
    model = argModel;
    bounds = argBounds;
  }

  makeWindow {
    if (window.notNil) { window.onClose = nil; window.close };

    window = Window("Prophet '08", bounds.resizeBy(60, 0)).front
      .background_(Color.hsv(0.05, 0.9, 0.5))
      .onClose_({ model.removeDependant(this) });
    view = P08View(window, bounds.moveBy(30, 0));

    view.addDependant(this);
    model.addDependant(this);
  }

  refreshView {
    model.ids.do { |param|
      this.prUpdateView(param);
    };
  }

  refreshViewDefaults {
    model.ids.do { |param|
      this.prUpdateView(param, true);
    };
  }

  close {
    window.close;
  }

  update { |object, param|
    if (object.class == Prophet08) {
      defer { this.prUpdateView(param) };
    };
    if (object.class == P08View) {
      //[param.id, param.value].postln;
      if (param.id.isInteger) {
        model.ids[param.id].midiValue = param.value;
      };
    };
  }

  prUpdateView { |param, default = false|
    var guiParam = view.ids[param.id];
    if (guiParam.notNil) {
      if (default) {
        guiParam.defaultValue = param.midiValue;
      } {
        if (guiParam.value != param.midiValue) {
          guiParam.value = param.midiValue;
        };
      };
    };
  }
}
