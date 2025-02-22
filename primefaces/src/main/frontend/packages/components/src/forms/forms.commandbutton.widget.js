/**
 * __PrimeFaces CommandButton Widget__
 * 
 * CommandButton is an extended version of standard commandButton with AJAX and theming.
 *
 * @forcedProp {number} [ajaxCount] Number of concurrent active Ajax requests.
 *
 * @interface {PrimeFaces.widget.CommandButtonCfg} cfg The configuration for the {@link  CommandButton| CommandButton widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 @prop {boolean} cfg.validateClientDynamic When set to `true` this button is only enabled after successful client side validation, otherwise classic behaviour. Used together with p:clientValidator.
 */
PrimeFaces.widget.CommandButton = class CommandButton extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        PrimeFaces.skinButton(this.jq);

        this.bindTriggers();

        if (cfg.validateClientDynamic) {
            let that = this;

            // init enabled/disabled-state after initial page-load
            PrimeFaces.queueTask(() => PrimeFaces.validation.validateButtonCsvRequirements(that.jq[0]), 0)

            // update enabled/disabled-state after ajax-updates
            PrimeFaces.validation.bindAjaxComplete();
        }
    }

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh(cfg) {
        $(document).off('pfAjaxSend.' + this.id + ' pfAjaxComplete.' + this.id);

        super.refresh(cfg);
    }

    /**
     * Sets up the global event listeners on the button.
     * @private
     */
    bindTriggers() {
        PrimeFaces.bindButtonInlineAjaxStatus(this, this.jq);
    }

    /**
     * Disables this button so that the user cannot press the button anymore.
     */
    disable() {
        PrimeFaces.utils.disableButton(this.jq);
    }

    /**
     * Enables this button so that the user can press the button.
     */
    enable() {
        PrimeFaces.utils.enableButton(this.jq);
    }

}
