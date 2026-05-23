import React from "react";
import { Button, Card, Col, Row } from "react-bootstrap";

import CurrentAnnouncements from "main/components/Announcement/CurrentAnnouncements";
import { hasRole } from "main/utils/currentUser";
import { daysSinceTimestamp } from "main/utils/dateUtils";

export default function CommonsOverview({
  commonsPlus,
  currentUser,
  currentAnnouncements,
}) {

  const leaderboardButtonClick = () => {
  window.location.href = `/leaderboard/${commonsPlus.commons.id}`;
};

  const showLeaderboard =
    hasRole(currentUser, "ROLE_ADMIN") || commonsPlus.commons.showLeaderboard;

  return (
    <Card data-testid="CommonsOverview">
      <Card.Header as="h5" className="woodenboardtable">
        Announcements
      </Card.Header>
      <Card.Body
        style={
          // Stryker disable next-line all: don't test CSS params
          { backgroundColor: "rgb(245, 210, 140)" }
        }
      >
        <CurrentAnnouncements announcements={currentAnnouncements} />

        <Row>
          <Col>
            <Card.Title>
              Today is day
              {daysSinceTimestamp(commonsPlus.commons.startingDate)}!
            </Card.Title>
            <Card.Text>Total Players: {commonsPlus.totalUsers}</Card.Text>
          </Col>
          <Col>
            {showLeaderboard && (
              <Button
                variant="outline-success"
                data-testid="user-leaderboard-button"
                onClick={leaderboardButtonClick}
              >
                Leaderboard
              </Button>
            )}
          </Col>
        </Row>
      </Card.Body>
    </Card>
  );
}