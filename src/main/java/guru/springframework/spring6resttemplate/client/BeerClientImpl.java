package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImpl;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BeerClientImpl implements BeerClient {

    public static final String GET_BEER_PATH = "/api/v1/beer";
    public static final String GET_BEER_BY_ID_PATH = "/api/v1/beer/{beerId}";
    private final RestTemplateBuilder restTemplateBuilder;

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 25;

    @Override
    public BeerDTO getBeerById(UUID beerId) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        return restTemplate.getForObject(GET_BEER_BY_ID_PATH, BeerDTO.class, beerId);
    }

    @Override
    public BeerDTO createBeer(BeerDTO newDto) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        URI uri = restTemplate.postForLocation(GET_BEER_PATH, newDto);

        assert uri != null;

        return restTemplate.getForObject(uri.getPath(), BeerDTO.class);
    }

    @Override
    public BeerDTO updateBeer(BeerDTO beerDTO) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        restTemplate.put(GET_BEER_BY_ID_PATH, beerDTO, beerDTO.getId());

        return getBeerById(beerDTO.getId());
    }

    @Override
    public void deleteBeer(UUID beerId) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.delete(GET_BEER_BY_ID_PATH, beerId);
    }

    @Override
    public Page<BeerDTO> listBeers() {
//        response.getBody().findPath("content").elements()
//                .forEachRemaining(node -> {
//                    System.out.println(node.get("beerName").asText());
//                });

        return this.listBeers(null, null, null, null, null);
    }

    @Override
    public Page<BeerDTO> listBeers(String beerName,
                                   BeerStyle beerStyle,
                                   Boolean showInventory,
                                   Integer pageNumber,
                                   Integer pageSize) {

        RestTemplate restTemplate = restTemplateBuilder.build();

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(GET_BEER_PATH);

        buildPageRequest(uriComponentsBuilder, pageNumber, pageSize);

        addBeerNameQueryParameter(beerName, uriComponentsBuilder);
        addBeerStyleQueryParameter(beerStyle, uriComponentsBuilder);
        addShowInventoryQueryParameter(showInventory, uriComponentsBuilder);

        ResponseEntity<BeerDTOPageImpl> response =
                restTemplate.getForEntity(uriComponentsBuilder.toUriString(), BeerDTOPageImpl.class);

        return response.getBody();
    }

    private static void addShowInventoryQueryParameter(Boolean showInventory, UriComponentsBuilder uriComponentsBuilder) {
        if (showInventory != null && showInventory)
            uriComponentsBuilder.queryParam("showInventory", true);
    }

    private static void addBeerStyleQueryParameter(BeerStyle beerStyle, UriComponentsBuilder uriComponentsBuilder) {
        if (beerStyle != null)
            uriComponentsBuilder.queryParam("beerStyle", beerStyle);
    }

    private static void addBeerNameQueryParameter(String beerName, UriComponentsBuilder uriComponentsBuilder) {
        if (beerName != null)
            uriComponentsBuilder.queryParam("beerName", beerName);
    }

    @Override
    public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(GET_BEER_PATH);

        addBeerNameQueryParameter(beerName, uriComponentsBuilder);
        addBeerStyleQueryParameter(beerStyle, uriComponentsBuilder);

        ResponseEntity<BeerDTOPageImpl> response =
                restTemplate.getForEntity(uriComponentsBuilder.toUriString(), BeerDTOPageImpl.class);

        return response.getBody();
    }

    @Override
    public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(GET_BEER_PATH);

        addBeerNameQueryParameter(beerName, uriComponentsBuilder);
        addBeerStyleQueryParameter(beerStyle, uriComponentsBuilder);
        addShowInventoryQueryParameter(showInventory, uriComponentsBuilder);

        ResponseEntity<BeerDTOPageImpl> response =
                restTemplate.getForEntity(uriComponentsBuilder.toUriString(), BeerDTOPageImpl.class);

        return response.getBody();
    }

    private void buildPageRequest(UriComponentsBuilder uriComponentsBuilder,
                                 Integer pageNumber,
                                 Integer pageSize) {
        int queryPageNumber;
        int queryPageSize;

        if (pageNumber != null && pageNumber > 0) {
            queryPageNumber = pageNumber - 1;
        } else {
            queryPageNumber = DEFAULT_PAGE;
        }

        uriComponentsBuilder.queryParam("pageNumber", queryPageNumber);

        if (pageSize == null) {
            queryPageSize = DEFAULT_PAGE_SIZE;
        } else {
            if (pageSize > 1000) {
                queryPageSize = 1000;
            } else {
                queryPageSize = pageSize;
            }
        }

        uriComponentsBuilder.queryParam("pageSize", queryPageSize);
    }
}
