Ext.define('LC.controller.Home',{
    extend:'Ext.app.Controller',

    requires:[
        'Ext.util.GeoLocation',
        'Ext.Ajax'
    ],

    config: {

        refs: {
            search:'#searchbutton'
        },

        control: {
            search: {
                tap:'doLocationSearch'
            }
        }
    },

    launch: function() {
        var me = this;
        me.geo = Ext.create('Ext.util.Geolocation', {
            autoUpdate: false
        });
    },

    doLocationSearch:function(button) {
        this.geo.updateLocation(function (geo) {
            if( geo != null ) {
                Ext.Ajax.request({
                    url:'/mobile/restaurants/geolocationsearch.ajax',
                    method:'GET',
                    params:{
                        longitude: geo.getLongitude(),
                        latitude: geo.getLatitude()
                    },
                    success: function(response) {
                        var obj = Ext.decode(response.responseText);
                        alert('Got count: ' + obj.count);
                    }
                });
            }
            else {
                alert('Could not determine location');
            }
        });
    }

});