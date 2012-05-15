package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.model.Address;
import com.ezar.clickandeat.model.User;

public interface AddressRepositoryCustom {

    Address saveWithLocationLookup(Address address);
}
