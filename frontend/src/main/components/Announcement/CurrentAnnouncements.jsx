import React, { useState } from "react";
import { Button } from "react-bootstrap";

const MAX_ANNOUNCEMENT_LENGTH = 120;

export default function CurrentAnnouncements({ announcements }) {
  const [expandedAnnouncements, setExpandedAnnouncements] = useState({});

  if (!announcements || announcements.length === 0) {
    return null;
  }

  const toggleExpanded = (announcementId) => {
    setExpandedAnnouncements((currentExpandedAnnouncements) => ({
      ...currentExpandedAnnouncements,
      [announcementId]: !currentExpandedAnnouncements[announcementId],
    }));
  };

  const getAnnouncementText = (announcement) => {
    return announcement.announcementText || "";
  };

  const getDisplayText = (announcement) => {
    const announcementText = getAnnouncementText(announcement);

    if (announcementText.length <= MAX_ANNOUNCEMENT_LENGTH) {
      return announcementText;
    }

    if (expandedAnnouncements[announcement.id]) {
      return announcementText;
    }

    return `${announcementText.substring(0, MAX_ANNOUNCEMENT_LENGTH)}.`;
  };

  return (
    <div data-testid="CurrentAnnouncements" className="mb-3">
      {announcements.map((announcement) => {
        const announcementText = getAnnouncementText(announcement);
        const shouldShowToggle =
          announcementText.length > MAX_ANNOUNCEMENT_LENGTH;

        return (
          <div
            key={announcement.id}
            className="mb-2"
            data-testid={`CurrentAnnouncements-announcement-${announcement.id}`}
          >
            {getDisplayText(announcement)}
            {shouldShowToggle && (
              <>
                {" "}
                <Button
                  variant="link"
                  className="p-0 align-baseline"
                  data-testid={`CurrentAnnouncements-toggle-${announcement.id}`}
                  onClick={() => toggleExpanded(announcement.id)}
                >
                  {expandedAnnouncements[announcement.id]
                    ? "Show less"
                    : "Show more"}
                </Button>
              </>
            )}
          </div>
        );
      })}
    </div>
  );
}