package subway.shortestpathfinder.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;
import subway.line.domain.Line;
import subway.section.domain.Section;
import subway.station.domain.Station;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ShortestPathFinder {
    private final Set<Line> lines;
    
    public ShortestPathFinder(final Set<Line> lines) {
        this.lines = lines;
    }
    
    public ShortestPathResult getShortestPath(
            final String startStationName,
            final String endStationName,
            final AgeGroupFeeCalculator ageGroupFeeCalculator
    ) {
        final WeightedMultigraph<Station, Section> graph = new WeightedMultigraph<>(Section.class);
        final DijkstraShortestPath<Station, Section> path = new DijkstraShortestPath<>(graph);
        lines.forEach(line -> line.addLineToGraph(graph));
        validateExistStation(startStationName, graph);
        validateExistStation(endStationName, graph);
        final GraphPath<Station, Section> graphPath = path.getPath(new Station(startStationName), new Station(endStationName));
        
        final List<String> shortestPath = getShortestPath(graphPath, startStationName, endStationName);
        final Long shortestDistance = (long) graphPath.getWeight();
        final Long fee = ageGroupFeeCalculator.calculateFee(
                ShortestPathCalculator.calculateFee(shortestDistance)
                        + getExtraCharge(graphPath)
        );
        return new ShortestPathResult(shortestPath, shortestDistance, fee);
    }
    
    private void validateExistStation(final String stationName, final WeightedMultigraph<Station, Section> graph) {
        if (!graph.containsVertex(new Station(stationName))) {
            throw new IllegalArgumentException(stationName + "은 노선에 등록되지 않은 역입니다.");
        }
    }
    
    private List<String> getShortestPath(
            final GraphPath<Station, Section> graphPath,
            final String startStationName,
            final String endStationName
    ) {
        try {
            return graphPath.getVertexList().stream()
                    .map(Station::getName)
                    .collect(Collectors.toUnmodifiableList());
        } catch (final NullPointerException ne) {
            throw new IllegalArgumentException(startStationName + "과 " + endStationName + "은 이어져있지 않습니다.");
        }
    }
    
    private long getExtraCharge(final GraphPath<Station, Section> graphPath) {
        return graphPath.getEdgeList().stream()
                .map(Section::getLineName)
                .map(this::getMatchLine)
                .mapToLong(Line::getExtraCharge)
                .max()
                .orElseThrow(() -> new IllegalArgumentException("경로의 구간이 존재하지 않습니다."));
    }
    
    private Line getMatchLine(final String lineName) {
        return lines.stream()
                .filter(line ->  line.isSameName(lineName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선 이름입니다."));
    }
    
    public Set<Line> getLines() {
        return lines;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ShortestPathFinder that = (ShortestPathFinder) o;
        return Objects.equals(lines, that.lines);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(lines);
    }
    
    @Override
    public String toString() {
        return "ShortestPathFinder{" +
                "lines=" + lines +
                '}';
    }
}
