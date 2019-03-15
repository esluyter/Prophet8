P08Background : SCViewHolder {
  *new { |parent, bounds|
    ^super.new.init(parent, bounds);
  }

  init { |parent, bounds|
    bounds = bounds ?? Rect(0, 0, parent.bounds.width, parent.bounds.height);
    view = UserView(parent, bounds)
      .background_(Color.hsv(0.6, 0.3, 0.2))
      .drawFunc_({ |view|
        Pen.color = Color.hsv(0.8, 0.3, 0.1);
        Pen.addRect(Rect(0, 0, 20, bounds.height));
        Pen.addRect(Rect(bounds.width - 20, 0, 20, bounds.height));
        Pen.fill;
        Pen.color = Color.hsv(0.05, 0.9, 0.5);
        Pen.addOval(Rect(3, 100, 14, 14));
        Pen.addOval(Rect(3, bounds.height - 114, 14, 14));
        Pen.addOval(Rect(bounds.width - 17, 100, 14, 14));
        Pen.addOval(Rect(bounds.width - 17, bounds.height - 114, 14, 14));
        Pen.fill;
      });
  }
}

P08View : SCViewHolder {
  var <oscView, <filtView, <ampView, <env3View, <seqView, <lfoViews, <modViews,
  <ctrlView, <lcdView, <topPanel, insignia, layerPanel, <ids;

  *new { |parent, bounds|
    ^super.new.init(parent, bounds);
  }

  init { |parent, bounds|
    P08GUIParam.resetIds;

    lfoViews = nil!4; modViews = nil!4;

    view = P08Background(parent, bounds);

    lcdView = P08LCDView(view, Rect(442, 150, 200, 70));

    topPanel = P08TopPanel(view, Rect(20, 0, 635, 180));
    4.do { |i|
      lfoViews[i] = P08LFOModule(view, Rect((i / 2).floor * 205 + 20, (i % 2) * 85 + 60, 210, 90), i);
    };

    ctrlView = P08CtrlModule(view, Rect(650, 0, 175, 235));
    4.do { |i|
      modViews[i] = P08ModModule(view, Rect(820, i * 57, 240, 62), i);
    };

    env3View = P08Env3Module(view, Rect(20, 230, 450, 105));
    layerPanel = P08LayerModule(view, Rect(465, 225, 120, 110));
    insignia = P08Insignia(view, Rect(348, 332, 234, 30));
    seqView = P08SeqModule(view, Rect(580, 230, 480, 135));

    oscView = P08OscModule(view, Rect(20, 330, 330, 200));
    filtView = P08FilterModule(view, Rect(345, 360, 390, 170));
    ampView = P08AmpModule(view, Rect(730, 360, 330, 170));

    ([oscView, filtView, ampView, env3View, seqView, ctrlView, topPanel, layerPanel] ++ lfoViews ++ modViews).do { |child|
      child.lcdView_(lcdView);
      child.prRegisterDependant(this);
    };

    ids = P08GUIParam.ids;
    P08GUIParam.resetIds;
  }

  update { |guiParam|
    this.changed(guiParam);
  }
}
