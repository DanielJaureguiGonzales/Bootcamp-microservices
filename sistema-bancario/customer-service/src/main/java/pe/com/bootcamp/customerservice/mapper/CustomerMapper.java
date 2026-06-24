package pe.com.bootcamp.customerservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import pe.com.bootcamp.customerservice.dto.CustomerRequest;
import pe.com.bootcamp.customerservice.dto.CustomerResponse;
import pe.com.bootcamp.customerservice.model.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerResponse toCustomerResponse(Customer customer);

    Customer toCustomer(CustomerRequest customerRequest);

    Customer updateCustomer(@MappingTarget Customer customer, CustomerRequest customerRequest);
}
