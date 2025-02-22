/**
 * __PrimeFaces TabMenu Widget__
 * 
 * TabMenu is a navigation component that displays menuitems as tabs.
 * 
 * @prop {JQuery} items The DOM elements for the tab menu entries.
 * 
 * @interface {PrimeFaces.widget.TabMenuCfg} cfg The configuration for the {@link  TabMenu| TabMenu widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.MenuCfg} cfg
 */
PrimeFaces.widget.TabMenu = class TabMenu extends PrimeFaces.widget.Menu {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        this.items = this.jq.find('> .ui-tabmenu-nav > li:not(.ui-state-disabled)');

        this.bindEvents();
        this.bindKeyEvents();
    }

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents() {
        this.items.on('mouseover.tabmenu', function(e) {
                    var element = $(this);
                    if(!element.hasClass('ui-state-active')) {
                        element.addClass('ui-state-hover');
                    }
                })
                .on('mouseout.tabmenu', function(e) {
                    $(this).removeClass('ui-state-hover');
                });
    }

    /**
     * Sets up all keyboard event listeners that are required by this widget.
     * @private
     */
    bindKeyEvents() {
        /* For Keyboard accessibility and Screen Readers */
        this.items.attr('tabindex', 0);

        this.items.on('focus.tabmenu', function(e) {
            $(this).addClass('ui-menuitem-outline');
        })
        .on('blur.tabmenu', function(){
            $(this).removeClass('ui-menuitem-outline');
        })
        .on('keydown.tabmenu', function(e) {
            if (PrimeFaces.utils.isActionKey(e)) {
                var currentLink = $(this).children('a');
                currentLink.trigger('click');
                PrimeFaces.utils.openLink(e, currentLink);
            }
        });
    }
}
