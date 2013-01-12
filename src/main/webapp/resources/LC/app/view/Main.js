Ext.define('LC.view.Main', {
    extend: 'Ext.tab.Panel',
    xtype: 'main',
    requires: [
        'Ext.TitleBar',
        'Ext.Video'
    ],
    config: {
        fullscreen: true,
        tabBarPosition: 'bottom',
        cls:'home',

        items: [{
            title: 'Home',
            iconCls: 'home',
            html: [
                '<img src="http://staging.sencha.com/img/sencha.png" />',
                '<h1>Welcome to Sencha Touch</h1>',
                "<p>You're creating the Getting Started app. This demonstrates how ",
                "to use tabs, lists and forms to create a simple app</p>",
                '<h2>Sencha Touch (2.0.0)</h2>'
            ].join("")
        },{
            xtype: 'nestedlist',
            title: 'Blog',
            iconCls: 'star',
            displayField: 'title',



            store: {
                type: 'tree',
                fields: [
                    'title', 'link', 'author', 'contentSnippet', 'content',
                    {name: 'leaf', defaultValue: true}
                ],
                root: {
                    leaf: false
                },
                proxy: {
                    type: 'jsonp',
                    url: 'https://ajax.googleapis.com/ajax/services/feed/load?v=1.0&q=http://feeds.feedburner.com/SenchaBlog',
                    reader: {
                        type: 'json',
                        rootProperty: 'responseData.feed.entries'
                    }
                }
            },

            listConfig: {
              variableHeights: true
            },

            detailCard: {
                xtype: 'panel',
                scrollable: true,
                styleHtmlContent: true
            },

            listeners: {
                itemtap: function(nestedList, list, index, element, post) {
                    this.getDetailCard().setHtml(post.get('content'));
                }
            }
        },{
            title: 'Contact',
            iconCls: 'user',
            xtype: 'formpanel',
            url: 'contact.php',
            layout: 'vbox',

            items: [{
                xtype: 'fieldset',
                title: 'Contact Us',
                instructions: '(email address is optional)',
                items: [{
                    xtype: 'textfield',
                    label: 'Name'
                },{
                    xtype: 'emailfield',
                    label: 'Email'
                },{
                    xtype: 'textareafield',
                    label: 'Message'
                }]
            },{
                xtype: 'button',
                text: 'Send',
                ui: 'confirm',
                handler: function() {
                    this.up('formpanel').submit();
                }
            }]
        }]
    }
});
