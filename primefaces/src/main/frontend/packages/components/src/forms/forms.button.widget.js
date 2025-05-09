/**
 * __PrimeFaces Button Widget__
 * 
 * Button is an extension to the standard h:button component with skinning capabilities.
 * 
 * @interface {PrimeFaces.widget.ButtonCfg} cfg The configuration for the {@link  Button| Button widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.Button = class Button extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        PrimeFaces.skinButton(this.jq);
    }

    /**
     * Enables this button so that the user cannot press it.
     */
    disable() {
        PrimeFaces.utils.disableButton(this.jq);
    }

    /**
     * Enables this button so that the user can press it.
     */
    enable() {
        PrimeFaces.utils.enableButton(this.jq);
    }

}
