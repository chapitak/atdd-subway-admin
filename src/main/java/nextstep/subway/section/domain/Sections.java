package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    public static final int DIFFERENCE_SECTIONS_STATIONS_SIZE = 1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Station fistStation = getFistStation(sections);
        stations.add(fistStation);
        Station upStation = fistStation;
        while (stations.size() != sections.size() + DIFFERENCE_SECTIONS_STATIONS_SIZE) {
            upStation = getUpStation(stations, upStation);
        }
        return stations;
    }

    private Station getUpStation(List<Station> stations, Station upStation) {
        for (Section section : sections) {
            upStation = getDownStation(stations, upStation, section);
        }
        return upStation;
    }

    private Station getDownStation(List<Station> stations, Station upStation, Section section) {
        if (upStation.equals(section.getUpStation())) {
            Station downStation = section.getDownStation();
            stations.add(downStation);
            upStation = downStation;
        }
        return upStation;
    }

    private Station getFistStation(List<Section> sections) {
        List<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        upStations.removeAll(downStations);

        return upStations.get(0);
    }
}
