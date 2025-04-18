/**
 * __PrimeFaces InputMask Widget__
 * 
 * InputMask forces an input to fit in a defined mask template.
 * 
 * @interface {PrimeFaces.widget.InputMaskCfg} cfg The configuration for the {@link  InputMask| InputMask widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * @extends {Inputmask.Options} cfg
 * 
 * @prop {string} cfg.mask The mask template to use.
 * @prop {boolean} hasFloatLabel Is this component wrapped in a float label.
 */
PrimeFaces.widget.InputMask = class InputMask extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        this.hasFloatLabel = PrimeFaces.utils.hasFloatLabel(this.jq);

        this.applyMask();

        //Visuals
        PrimeFaces.skinInput(this.jq);
    }
    

    /**
     * @override
     * @inheritdoc
     */
    destroy() {
        this.jq.inputmask('remove');

        super.destroy();
    }

    /**
     * Applys the mask to the input.
     * @private
     */
    applyMask() {
        if (this.hasFloatLabel) {
            this.cfg.showMaskOnHover = false;
        }
        if(this.cfg.mask && !this.jq.is('[readonly]') && !this.jq.is(':disabled')) {
            this.cfg.clearIncomplete = (this.cfg.autoClear === undefined) ? true : this.cfg.autoClear;
            this.jq.inputmask('remove').inputmask(this.cfg);
        }
    }

    /**
     * Sets the value of this input field to the given value. If the value does not fit the mask, it is adjusted
     * appropriately.
     * @param {string} value New value to set on this input field
     */
    setValue(value) {
        this.jq.inputmask("setvalue", value);
    }

    /**
     * Returns the current value of this input field including the mask like "12/31/1999".
     * @return {string} The current value of this input field with mask.
     */
    getValue() {
        return this.jq.val();
    }

    /**
     * Returns the current value of this input field without the mask like "12311999".
     * @return {string} The current value of this input field without mask.
     */
    getValueUnmasked() {
        return this.jq.inputmask('unmaskedvalue');
    }

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    disable() {
        this.jq.inputmask('remove');
        PrimeFaces.utils.disableInputWidget(this.jq);
    }

    /**
     * Enables this input so that the user can enter a value.
     */
    enable() {
        PrimeFaces.utils.enableInputWidget(this.jq);
        this.applyMask();
    }

}
