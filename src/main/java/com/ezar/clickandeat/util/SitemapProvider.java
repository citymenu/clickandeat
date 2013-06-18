package com.ezar.clickandeat.util;

import com.ezar.clickandeat.config.MessageFactory;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.repository.RestaurantRepository;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class SitemapProvider implements ApplicationListener<ContextRefreshedEvent> {
    
    private static final Logger LOGGER = Logger.getLogger(SitemapProvider.class);

    private static String sitemap;

    @Autowired
    private CuisineProvider cuisineProvider;
    
    @Autowired
    private RestaurantRepository restaurantRepository;

    private final Object lock = new Object();
    
    private final Timer timer = new Timer();

    private final String rootUrl = "http://www.llamarycomer.com";
    
    private final String lastModDate = DateTimeUtil.formatLocalDate(new LocalDate());

    private static AtomicBoolean initialized = new AtomicBoolean(false);
    
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(!initialized.getAndSet(true)) {
            timer.schedule(new TimerTask() {
                public void run() {
                    updateSitemap();
                }
            },0,1000 * 60 * 60 * 24);
        }
    }


    /**
     * Returns the sitemap when ready
     * @return
     */

    public String getSitemap() {
        while(sitemap == null) {
            synchronized (lock) {
                try {
                    lock.wait();
                }
                catch(InterruptedException ex ) {
                    // Ignore on purpose
                }
            }
        }
        return sitemap;
    }


    /**
     * Builds the sitemap
     */

    private void updateSitemap() {
        LOGGER.info("Updating sitemap xml");
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sb.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        appendUrl("/", "hourly", 1d, sb);
        
        // Add principal locations with priority 1
        List<Pair<String,String>> primaryLocations = cuisineProvider.getLocations();
        for(Pair<String,String> primaryLocation: primaryLocations) {
            String location = primaryLocation.first;
            String locationLink = MessageFactory.formatMessage("page.link.location",false,location).toLowerCase(MessageFactory.getLocale());
            appendUrl(locationLink, "daily", 1d,sb);
        }
        
        // Add locations priority .9 and cuisines by location priority .8
        for(Map.Entry<Pair<String,String>,List<Pair<String,String>>> entry: cuisineProvider.getCuisineLocations().entrySet()) {

            Pair<String,String> locationPair = entry.getKey();
            String location = locationPair.first;
            boolean isPrimaryLocation = primaryLocations.contains(locationPair);

            // Don't re-add a primary location, add lower ranked locations as priority .9
            if(!isPrimaryLocation) {
                String locationLink = MessageFactory.formatMessage("page.link.location",false,location).toLowerCase(MessageFactory.getLocale());
                appendUrl(locationLink, "daily", 0.9d,sb);
            }
            for(Pair<String,String> cuisine: entry.getValue()) {
                String csn = cuisine.first;
                String cuisineLink = MessageFactory.formatMessage("page.link.cuisine",false,csn,location).toLowerCase(MessageFactory.getLocale());
                double priority = isPrimaryLocation? 1d: .8d;
                appendUrl(cuisineLink, "daily", priority, sb);
            }
        }
        
        // Add direct links to restaurants priority .7
        for(Restaurant restaurant: restaurantRepository.quickLaunch()) {
            appendUrl(restaurant.getUrl(),"daily",0.7,sb);
        }
        
        sb.append("</urlset>");
        sitemap = sb.toString();
        LOGGER.info("Finished updating sitemap xml");
    }


    /**
     * @param loc
     * @param priority
     * @param sb
     */

    private void appendUrl(String loc, String freq, double priority, StringBuilder sb) {
        String href = (rootUrl + loc).replace("&","&amp;");
        sb.append("\t<url>\n");
        sb.append("\t\t<loc>").append(href).append("</loc>\n");
        sb.append("\t\t<lastmod>").append(lastModDate).append("</lastmod>\n");
        sb.append("\t\t<changefreq>").append(freq).append("</changefreq>\n");
        sb.append("\t\t<priority>").append(priority).append("</priority>\n");
        sb.append("\t</url>\n");
    }
    
    
}
