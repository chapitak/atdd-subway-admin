package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    public static final int DIFFERENCE_SECTIONS_STATIONS_SIZE = 1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    public void add(Section newSection) {
        if (existsUpStation(newSection)) {
            Section targetSection = getTargetUpSection(newSection);
            targetSection.setUpStation(newSection.getDownStation());
            targetSection.setDistance(targetSection.getDistance() - newSection.getDistance());
        }
        if (existsDownStation(newSection)) {
            Section targetSection = getTargetDownSection(newSection);
            targetSection.setUpStation(newSection.getDownStation());
            targetSection.setDistance(targetSection.getDistance() - newSection.getDistance());
        }
        sections.add(newSection);

    }

    private Section getTargetDownSection(Section newSection) {
        return sections.stream()
                .filter(section -> section
                        .getDownStation() == newSection
                        .getDownStation())
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private boolean existsDownStation(Section newSection) {
        return sections.stream()
                .map(Section::getDownStation)
                .anyMatch(station -> station == newSection.getDownStation());
    }

    private Section getTargetUpSection(Section newSection) {
        return sections.stream()
                .filter(section -> section
                        .getUpStation() == newSection
                        .getUpStation())
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private boolean existsUpStation(Section newSection) {
        return sections.stream()
                .map(Section::getUpStation)
                .anyMatch(station -> station == newSection.getUpStation());
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
        Station finalUpStation = upStation;
        Station downStation = sections.stream()
                .filter(section1 -> Objects.equals(section1.getUpStation(), finalUpStation))
                .map(Section::getDownStation)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        stations.add(downStation);
        upStation = downStation;
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

    public List<SectionResponse> getSectionsResponses() {
        return sections.stream().map(SectionResponse::of).collect(Collectors.toList());
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public Sections() {
    }

    public void addAndSetLine(Section section, Line line) {
        sections.add(section);
        section.setLine(line);
    }
}
