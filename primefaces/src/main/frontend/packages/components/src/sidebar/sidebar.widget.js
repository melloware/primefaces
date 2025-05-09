/**
 * __PrimeFaces Sidebar Widget__
 *
 * Sidebar is a panel component displayed as an overlay at the edges of the screen.
 *
 * @prop {JQuery} closeIcon The DOM element for the icon that closes this sidebar.
 * @prop {boolean} loaded When dynamic loading is enabled, whether the content was already loaded.
 *
 * @typedef PrimeFaces.widget.Sidebar.OnHideCallback Callback that is invoked when the sidebar is opened. See also
 * {@link SidebarCfg.onHide}.
 * @this {PrimeFaces.widget.Sidebar} PrimeFaces.widget.Sidebar.OnHideCallback
 *
 * @typedef PrimeFaces.widget.Sidebar.OnShowCallback Callback that is invoked when the sidebar is closed. See also
 * {@link SidebarCfg.onShow}.
 * @this {PrimeFaces.widget.Sidebar} PrimeFaces.widget.Sidebar.OnShowCallback
 *
 * @interface {PrimeFaces.widget.SidebarCfg} cfg The configuration for the {@link  Sidebar| Sidebar widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DynamicOverlayWidgetCfg} cfg
 *
 * @prop {boolean} cfg.modal Whether the sidebar is modal and blocks the main content and other dialogs.
 * @prop {boolean} cfg.showCloseIcon Whether the close icon is displayed.
 * @prop {string} cfg.appendTo The search expression for the element to which the overlay panel should be appended.
 * @prop {number} cfg.baseZIndex Base z-index for the sidebar.
 * @prop {PrimeFaces.widget.Sidebar.OnHideCallback} cfg.onHide Callback that is invoked when the sidebar is opened.
 * @prop {PrimeFaces.widget.Sidebar.OnShowCallback} cfg.onShow Callback that is invoked when the sidebar is closed.
 * @prop {boolean} cfg.visible Whether the sidebar is initially opened.
 * @prop {boolean} cfg.dynamic `true` to load the content via AJAX when the overlay panel is opened, `false` to load
 * the content immediately.
 *
 * @prop {JQuery} content DOM element of the container for the content of this sidebar.
 */
PrimeFaces.widget.Sidebar = class Sidebar extends PrimeFaces.widget.DynamicOverlayWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        this.cfg.modal = (this.cfg.modal === true || this.cfg.modal === undefined);
        this.cfg.showCloseIcon = (this.cfg.showCloseIcon === true || this.cfg.showCloseIcon === undefined);
        this.cfg.baseZIndex = this.cfg.baseZIndex||0;

        this.content = this.jq.children('.ui-sidebar-content');

        if(this.cfg.showCloseIcon) {
            this.closeIcon = this.jq.children('.ui-sidebar-close');
        }

        //aria
        this.applyARIA();

        if(this.cfg.visible){
            this.show();
        }

        this.bindEvents();
    }

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh(cfg) {
        super.refresh(cfg);

        this.loaded = false;
    }

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents() {
        var $this = this;

        if(this.cfg.showCloseIcon) {
            this.closeIcon.off('mouseover mouseout focus blur click').on('mouseover', function() {
                $(this).addClass('ui-state-hover');
            }).on('mouseout', function() {
                $(this).removeClass('ui-state-hover');
            }).on('focus', function() {
                $(this).addClass('ui-state-focus');
            }).on('blur', function() {
                $(this).removeClass('ui-state-focus');
            }).on('click', function(e) {
                $this.hide();
                e.preventDefault();
            });
        }
    }

    /**
     * Brings up this sidebar in case is is not already visible.
     * @param {boolean} reload If the dynamic content should be reloaded.
     */
    show(reload = false) {
        if(this.isVisible()) {
            return;
        }

        if ((!this.loaded || reload === true) && this.cfg.dynamic) {
            this.loadContents();
        }
        else {
            this._show();
        }
    }

    /**
     * Makes the sidebar panel visible.
     * @private
     */
    _show() {
        this.jq.addClass('ui-sidebar-active');
        this.jq.css('z-index', String(this.cfg.baseZIndex + (++PrimeFaces.zindex)));

        this.postShow();

        if(this.cfg.modal) {
            this.enableModality();
        }
    }

    /**
     * Callback function that is invoked when this sidebar is hidden.
     * @private
     */
    postShow() {
        this.callBehavior('open');

        PrimeFaces.invokeDeferredRenders(this.id);

        //execute user defined callback
        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
    }

    /**
     * Hides this sidebara in case it is not already hidden.
     */
    hide() {
        if(!this.isVisible()) {
            return;
        }

        this.jq.removeClass('ui-sidebar-active');
        this.onHide();

        if(this.cfg.modal) {
            this.disableModality();
        }
    }

    /**
     * Checks whether this sidebar is currently visible.
     * @return {boolean} `true` if this sideplay is visible, or `false` otherwise.
     */
    isVisible() {
        return this.jq.hasClass('ui-sidebar-active');
    }

    /**
     * Callback function that is invoked when this sidebar is hidden.
     * @private
     * @param {JQuery.TriggeredEvent} event Currently unused.
     * @param {unknown} ui Currently unused.
     */
    onHide(event, ui) {
        this.callBehavior('close');

        if(this.cfg.onHide) {
            this.cfg.onHide.call(this, event, ui);
        }
    }

    /**
     * Hides this sidebar if it is visible or brings it up if it is hidden.
     */
    toggle() {
        if(this.isVisible())
            this.hide();
        else
            this.show();
    }

    /**
     * @override
     * @inheritdoc
     */
    enableModality() {
        super.enableModality();

        var $this = this;
        this.modalOverlay.one('click.sidebar', function() {
            $this.hide();
        });
    }

    /**
     * @override
     * @inheritdoc
     * @return {JQuery}
     */
    getModalTabbables(){
        return this.jq.find(':tabbable');
    }

    /**
     * Sets all ARIA attributes on the elements and the icons.
     * @private
     */
    applyARIA() {
        this.jq.attr({
            'role': 'dialog'
            ,'aria-hidden': !this.cfg.visible
            ,'aria-modal': this.cfg.modal && this.cfg.visible
        });

        if(this.cfg.showCloseIcon) {
            PrimeFaces.skinCloseAction(this.closeIcon);
        }
    }

    /**
     * Loads the contents of this sidebar panel dynamically via AJAX, if dynamic loading is enabled.
     * @private
     */
    loadContents() {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            ignoreAutoUpdate: true,
            params: [
                {name: this.id + '_contentLoad', value: true}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.content.html(content);
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this.loaded = true;
                $this._show();
            }
        };

        if(this.hasBehavior('loadContent')) {
            this.callBehavior('loadContent', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

}